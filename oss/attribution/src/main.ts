import * as fs from "fs-extra";
import * as path from "path";
import axios from "axios";
import Handlebars from "handlebars";

// Regular expressions for finding license files
const licenseRegex = /^(LICENSE|LICENCE)($|\.md|\.txt)/i;
const licenseSpecificRegex = (licenseName: string) =>
  new RegExp(`^(LICENSE|LICENCE)([\\.-]${licenseName})(\\.md|\\.txt)?`, "i");
const notice = "NOTICE";

// Dictionary of generic licenses with their URLs
const genericLicenses: Record<string, string> = {
  "Apache-2.0": "https://www.apache.org/licenses/LICENSE-2.0.txt",
  MIT: "https://spdx.org/licenses/MIT.txt",
  "MIT-0": "https://raw.githubusercontent.com/aws/mit-0/master/MIT-0",
  ISC: "https://spdx.org/licenses/ISC.txt",
  "BSD-2-Clause": "https://spdx.org/licenses/BSD-2-Clause.txt",
  "BSD-3-Clause": "https://spdx.org/licenses/BSD-3-Clause.txt",
  "MPL-2.0": "https://www.mozilla.org/media/MPL/2.0/index.815ca599c9df.txt",
  "LGPL-2.1-or-later": "https://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt",
  "LGPL-2.1-only": "https://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt",
  "EPL-2.0": "https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt",
};

// Function to extract license from a file
function extractLicense(licensePath: string): string {
  return fs.readFileSync(licensePath, "utf-8");
}

// Function to find license in a directory
function findLicense(
  dirPath: string,
  licenseName: string | null,
): string | undefined {
  if (!fs.existsSync(dirPath)) {
    return undefined;
  }

  const files = fs.readdirSync(dirPath, { recursive: true });

  for (const file of files) {
    const filePath = path.join(dirPath, file as string);

    // Skip if it's a directory
    if (fs.statSync(filePath).isDirectory()) {
      continue;
    }

    const fileName = path.basename(filePath);

    // Check for license specific to the package
    if (licenseName && licenseSpecificRegex(licenseName).test(fileName)) {
      return extractLicense(filePath);
    }

    // Check for generic license file
    if (licenseRegex.test(fileName)) {
      return extractLicense(filePath);
    }
  }

  return undefined;
}

// Function to fetch license from URL
async function fetchHttpLicense(url: string): Promise<string | undefined> {
  try {
    const response = await axios.get(url);
    if (response.status !== 200) {
      console.error(`Failed to fetch ${url}`);
      return undefined;
    }
    return response.data;
  } catch (error) {
    console.error(`Failed to fetch ${url}`, error);
    return undefined;
  }
}

// Template for generating Markdown
const template = `# Open Source Software Attribution

This software depends on external packages and source code.
The applicable license information is listed below:

{{#each packages}}
----

### {{name}} @{{version}}{{#if url}} - {{url}}{{/if}}

{{{license}}}
{{#if source_url}}

[Source code]({{source_url}})
{{/if}}

{{/each}}
`;

// Interface for package data
interface PackageData {
  name: string;
  url: string | null;
  license: string;
  version: string;
  source_url: string | null;
}

// Interface for analyzer result
interface AnalyzerResult {
  analyzer: {
    result: {
      packages: Array<{
        id: string;
        homepage_url: string;
        source_artifact: {
          url: string;
        };
        vcs_processed: {
          url: string;
        };
        concluded_license?: string;
        declared_licenses_processed: {
          spdx_expression?: string;
        };
      }>;
    };
  };
}

async function main() {
  // Check command line arguments
  if (process.argv.length !== 4) {
    console.error("Usage: node main.js <base_path> <output_file>");
    process.exit(1);
  }

  const basePath = process.argv[2];
  const outputFile = process.argv[3];

  const srcPath = path.join(basePath, "src");
  const analyzerPath = path.join(basePath, "analyzer-result.json");

  const packages: PackageData[] = [];

  if (fs.existsSync(analyzerPath)) {
    const data: AnalyzerResult = fs.readJSONSync(analyzerPath);

    for (const package_ of data.analyzer.result.packages) {
      const id = package_.id;
      const [packageManager, group, packageName, version] = id.split(":");

      let name = "";
      let actualGroup = group;

      if (group === "") {
        actualGroup = "unknown";
        name = packageName;
      } else {
        name = `${group}:${packageName}`;
      }

      const url = package_.homepage_url;
      const sourceUrl = package_.source_artifact.url;
      const vcsUrl = package_.vcs_processed.url;

      const packageSrcPath = path.join(
        srcPath,
        packageManager,
        encodeURIComponent(actualGroup),
        encodeURIComponent(packageName),
        version,
      );

      let licenseName = null;

      if (package_.concluded_license) {
        licenseName = package_.concluded_license;
      } else if (package_.declared_licenses_processed.spdx_expression) {
        licenseName = package_.declared_licenses_processed.spdx_expression;
      }

      let licenseText = findLicense(packageSrcPath, licenseName);

      if (!licenseText && licenseName) {
        console.warn(`Warning: Defaulting to generic ${licenseName} for ${id}`);

        if (genericLicenses[licenseName]) {
          const licenseUrl = genericLicenses[licenseName];
          licenseText = await fetchHttpLicense(licenseUrl);
        } else {
          console.warn(`Warning: License ${licenseName} missing from URL map`);
        }
      }

      if (licenseText) {
        packages.push({
          name,
          url: url || vcsUrl || null,
          license: licenseText,
          version,
          source_url: sourceUrl || vcsUrl || null,
        });
      } else {
        console.warn(`Warning: No license entry for ${id}`);
      }
    }
  } else {
    console.error("Failed to find results file");
    process.exit(1);
  }

  console.log(`Writing results to ${outputFile}`);

  const compiledTemplate = Handlebars.compile(template);

  const output = compiledTemplate({ packages });

  fs.writeFileSync(outputFile, output);
  console.log("Finished!");
}

main().catch((error) => {
  console.error("Error:", error);
  process.exit(1);
});
