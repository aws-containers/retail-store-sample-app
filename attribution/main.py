import json
import os
import re
from jinja2 import Template

license = "LICENSE"
notice = "NOTICE"

generic_licenses = {
  "Apache-2.0": "https://www.apache.org/licenses/LICENSE-2.0.txt",
  "MIT": "https://spdx.org/licenses/MIT.txt",
  "ISC": "https://spdx.org/licenses/ISC.txt",
  "BSD-3-Clause": "https://spdx.org/licenses/BSD-3-Clause.txt",
  "MPL-2.0": "https://www.mozilla.org/media/MPL/2.0/index.815ca599c9df.txt"
}

def find_license(path):
  for root, directories, files in os.walk(path):

    for name in files:

      if re.search(license, name, re.IGNORECASE):
        if ".java" in name:
          break
        elif ".py" in name:
          break
        elif ".js" in name:
          break

        license_path = os.path.join(root, name)

        text_file = open(license_path, "r")

        data = text_file.read()
        
        #close file
        text_file.close()

        return data
      

template = """# Open Source Software Attribution

This software depends on external packages and source code.
The applicable license information is listed below:
{% for package in packages %}
----

### {{ package.name }} - {{ package.url }}

{{ package.license }}{% endfor %}
"""

component = 'checkout'

base_path = '../reports/license-report/{}'.format(component)
src_path = '{}/src'.format(base_path)

f = open('{}/analyzer-result.json'.format(base_path))

data = json.load(f)

packages = []

for i in data['analyzer']['result']['packages']:
  package = i['package']

  id = package['id']

  package_manager, group, package_name, version = id.split(':')

  name = ''

  if(group == ''):
    group = 'unknown'
    name = package_name
  else:
    name = '{}:{}'.format(group, package_name)

  url = package['vcs']['url']

  package_src_path = '{}/{}/{}/{}/{}'.format(src_path, package_manager, group, package_name,version)

  license_text = find_license(package_src_path)

  if(license_text is not None):
    packages.append({"name": name, "url": url, "license": license_text})
  else:
    print('Warning: Skipping package {}'.format(id))

j2_template = Template(template)

output_file = '{}/ATTRIBUTION.md'.format(base_path)

print('Writing results to {}'.format(output_file))

text_file = open(output_file, "w")
n = text_file.write(j2_template.render({"packages": packages}))
text_file.close()

print('Finished!')