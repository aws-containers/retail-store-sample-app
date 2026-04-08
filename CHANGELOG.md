# Changelog

## [1.4.0](https://github.com/madlinux768/retail-store-sample-app/compare/v1.3.0...v1.4.0) (2026-01-14)


### Features

* Add EventBridge lifecycle events for ECS Container Insights and update ADOT to CloudWatch Agent ([#913](https://github.com/madlinux768/retail-store-sample-app/issues/913)) ([549594b](https://github.com/madlinux768/retail-store-sample-app/commit/549594bf1f47d16f19a02ce040b55e4353dd8be6))
* Add headers, panic, echo and store utilities ([#728](https://github.com/madlinux768/retail-store-sample-app/issues/728)) ([c4f703b](https://github.com/madlinux768/retail-store-sample-app/commit/c4f703bc78bd832116a78e78bf44024aa5c361ca))
* Allow serving sample images from filesystem ([#853](https://github.com/madlinux768/retail-store-sample-app/issues/853)) ([43f3283](https://github.com/madlinux768/retail-store-sample-app/commit/43f3283f84ad0db99f75fa05e7eb7130c56d149e))
* Application v1 ([#742](https://github.com/madlinux768/retail-store-sample-app/issues/742)) ([2ea99fb](https://github.com/madlinux768/retail-store-sample-app/commit/2ea99fbf94c891c4da166c2527f082ab5c621240))
* Chaos testing endpoints ([#818](https://github.com/madlinux768/retail-store-sample-app/issues/818)) ([f8f2207](https://github.com/madlinux768/retail-store-sample-app/commit/f8f22078ea67049144bc2d59efc7a60c730c67f0))
* **ci:** add ECR repositories and image build pipeline ([6f54be6](https://github.com/madlinux768/retail-store-sample-app/commit/6f54be651b9bf2b2ef60051ab15a3312618da8ef))
* **ecs:** add ECS Managed Instances deployment ([b109743](https://github.com/madlinux768/retail-store-sample-app/commit/b109743de3604903b9e72a09f6f309a078d12b09))
* **observability:** enable Container Insights enhanced mode at account level - Add aws_ecs_account_setting_default for containerInsights - Set to 'enhanced' for additional task/container metrics ([7c0aa80](https://github.com/madlinux768/retail-store-sample-app/commit/7c0aa800a7a0b8d5e276db22122277ef4a89ec8f))
* Optimize asset image sizes ([#840](https://github.com/madlinux768/retail-store-sample-app/issues/840)) ([65a7748](https://github.com/madlinux768/retail-store-sample-app/commit/65a7748dfd99a1392baf788d2a059228a35062ce))
* **security:** add WAF IP allowlist for ALB ([576c469](https://github.com/madlinux768/retail-store-sample-app/commit/576c469378438e171d126c5c18d4035242e70bf3))
* **security:** add WAF with IP allowlist using GitHub secret ([63ffa92](https://github.com/madlinux768/retail-store-sample-app/commit/63ffa926ee786c9da228bee9bb14b07efd55131d))
* Upgraded checkout to NestJS v11 ([#842](https://github.com/madlinux768/retail-store-sample-app/issues/842)) ([4f1c921](https://github.com/madlinux768/retail-store-sample-app/commit/4f1c921320061e6e7716a14409fa3c640c98a917))
* **workflow:** add OpenTelemetry toggle to deployment workflow ([acbfbe5](https://github.com/madlinux768/retail-store-sample-app/commit/acbfbe59efdabbbd64a1bcf3b0b1bfc2176041e2))


### Bug Fixes

* Add UI teal theme color ([#923](https://github.com/madlinux768/retail-store-sample-app/issues/923)) ([b382620](https://github.com/madlinux768/retail-store-sample-app/commit/b382620fcc7753b0e9c5256e972bc0844e8d9039))
* **bootstrap:** add complete KMS permissions for Terraform ([53f8f7b](https://github.com/madlinux768/retail-store-sample-app/commit/53f8f7b85d467700c1cb7a8e86b99a739b4cc25c))
* **bootstrap:** add KMS ListResourceTags and ListKeys permissions ([35fc106](https://github.com/madlinux768/retail-store-sample-app/commit/35fc106592742d3288e3169e93197b5ef565ceec))
* **bootstrap:** add missing IAM permissions ([758cf7b](https://github.com/madlinux768/retail-store-sample-app/commit/758cf7b33835d531017c501c35b75973a19d92d6))
* **ci:** add backslash continuation for nx command ([0ab3e71](https://github.com/madlinux768/retail-store-sample-app/commit/0ab3e71323661174d44aea9869f4a36e52485b75))
* **ci:** build all services when workflow changes ([4932846](https://github.com/madlinux768/retail-store-sample-app/commit/493284653d62fef1f3f541602257bac86b4950ea))
* **ci:** correct image tag generation in workflow ([edc6a41](https://github.com/madlinux768/retail-store-sample-app/commit/edc6a41786fc56f7913ba977f0f52fdaff848d7b))
* **ci:** correct YAML indentation in build step ([11409dc](https://github.com/madlinux768/retail-store-sample-app/commit/11409dc801e95a8aa0000f0e9d709106ea24584a))
* **ci:** improve bash script robustness in build step ([adfd84d](https://github.com/madlinux768/retail-store-sample-app/commit/adfd84d12aabdbd22145553c802bdf2f5d3a264b))
* **ci:** remove debug step and disable k8s e2e tests ([b3c3614](https://github.com/madlinux768/retail-store-sample-app/commit/b3c361402d83d52e6b522b57fac79d30fac9fcb7))
* **ci:** remove trailing slash in debug step ([2dc3453](https://github.com/madlinux768/retail-store-sample-app/commit/2dc34533ce6809709e6c7850fd0d54231b68a213))
* **ci:** sanitize matrix.service to remove newlines ([6d9f12c](https://github.com/madlinux768/retail-store-sample-app/commit/6d9f12c136d18ef0f12af170e9e3a80fad94ff3e))
* **ci:** switch to nx container command for builds ([1eb9b27](https://github.com/madlinux768/retail-store-sample-app/commit/1eb9b27638aecbfb88e7d85ac09444e33a925a8e))
* **ci:** use comma-separated tags for nx container ([2a48c25](https://github.com/madlinux768/retail-store-sample-app/commit/2a48c255727fadd758177384dc2761ece4ff45c5))
* **ci:** use docker metadata action and add debug step ([2328a14](https://github.com/madlinux768/retail-store-sample-app/commit/2328a14fdbf10aeb72b82aef3d7a775e44d4b427))
* **ci:** use docker metadata action for tag generation ([51f83fa](https://github.com/madlinux768/retail-store-sample-app/commit/51f83fa80ad644459593a2c9aa3805cc53a4ebc7))
* Consistent OpenTelemetry versions in Java components ([5ea06b9](https://github.com/madlinux768/retail-store-sample-app/commit/5ea06b9900d2d4878f560673c3664cb1386d7fb9))
* **deps:** bump golang.org/x/crypto in /src/catalog ([#829](https://github.com/madlinux768/retail-store-sample-app/issues/829)) ([50ff85c](https://github.com/madlinux768/retail-store-sample-app/commit/50ff85c654aa7f4c4469d8fb27a28c2c96988214))
* **deps:** bump golang.org/x/net from 0.34.0 to 0.38.0 in /src/catalog ([#831](https://github.com/madlinux768/retail-store-sample-app/issues/831)) ([6303846](https://github.com/madlinux768/retail-store-sample-app/commit/63038463f862f2d18518c17b72355f53cf5b173c))
* **deps:** update dependency axios to v1.10.0 ([#874](https://github.com/madlinux768/retail-store-sample-app/issues/874)) ([4c0113e](https://github.com/madlinux768/retail-store-sample-app/commit/4c0113e8144252a068b199a7c00c0924ac52fb90))
* **deps:** update dependency axios to v1.8.4 ([#791](https://github.com/madlinux768/retail-store-sample-app/issues/791)) ([06fe506](https://github.com/madlinux768/retail-store-sample-app/commit/06fe506a860bdadbe7fa69251b87ff62878f7f5d))
* **deps:** update dependency de.codecentric:chaos-monkey-spring-boot to v3.1.4 ([#769](https://github.com/madlinux768/retail-store-sample-app/issues/769)) ([8aeeea4](https://github.com/madlinux768/retail-store-sample-app/commit/8aeeea4ec3bbd6ec93c3a13aea43d15d805c0c3c))
* **deps:** update dependency de.codecentric:chaos-monkey-spring-boot to v3.2.0 ([#810](https://github.com/madlinux768/retail-store-sample-app/issues/810)) ([aff5aa9](https://github.com/madlinux768/retail-store-sample-app/commit/aff5aa94a81923765d38f3a4dd7b639706be1563))
* **deps:** update dependency io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom to v2.17.0 ([#811](https://github.com/madlinux768/retail-store-sample-app/issues/811)) ([7ee50f7](https://github.com/madlinux768/retail-store-sample-app/commit/7ee50f71c86fe8bf27f5b7d3651e44d59c11086a))
* **deps:** update dependency io.swagger:swagger-annotations to v1.6.16 ([#849](https://github.com/madlinux768/retail-store-sample-app/issues/849)) ([17b44b6](https://github.com/madlinux768/retail-store-sample-app/commit/17b44b655bdd8011bc65d38301b720588042ead2))
* **deps:** update dependency org.openapitools:jackson-databind-nullable to v0.2.7 ([#926](https://github.com/madlinux768/retail-store-sample-app/issues/926)) ([46849a7](https://github.com/madlinux768/retail-store-sample-app/commit/46849a74089f06acad31222b6c4d7cdb8da32984))
* **deps:** update dependency org.projectlombok:lombok to v1.18.38 ([#850](https://github.com/madlinux768/retail-store-sample-app/issues/850)) ([2f76853](https://github.com/madlinux768/retail-store-sample-app/commit/2f768538e9ad409dba0ae4b1b83f76e3b0aed8b0))
* **deps:** update dependency org.projectlombok:lombok to v1.18.40 ([#927](https://github.com/madlinux768/retail-store-sample-app/issues/927)) ([4544834](https://github.com/madlinux768/retail-store-sample-app/commit/454483476947cc4e911f707969fdb898b4e9ae62))
* **deps:** update dependency org.springdoc:springdoc-openapi-starter-webmvc-ui to v2.8.9 ([#851](https://github.com/madlinux768/retail-store-sample-app/issues/851)) ([4a1a201](https://github.com/madlinux768/retail-store-sample-app/commit/4a1a2014222dd549850352f78851646830693143))
* **deps:** update dependency org.springframework.ai:spring-ai-bom to v1.0.2 ([#928](https://github.com/madlinux768/retail-store-sample-app/issues/928)) ([948ce82](https://github.com/madlinux768/retail-store-sample-app/commit/948ce82b2192135ca5c69bb4582011f176dbda1b))
* **deps:** update dependency org.springframework.boot:spring-boot-starter-parent to v3.4.4 ([#802](https://github.com/madlinux768/retail-store-sample-app/issues/802)) ([3a9b53f](https://github.com/madlinux768/retail-store-sample-app/commit/3a9b53f1a1387ea0bfeabd7d6495983f15922ac3))
* **deps:** update dependency org.springframework.boot:spring-boot-starter-parent to v3.5.3 ([#879](https://github.com/madlinux768/retail-store-sample-app/issues/879)) ([08120b1](https://github.com/madlinux768/retail-store-sample-app/commit/08120b10d311d5b30bbf3b30f7a80537ec61b912))
* **deps:** update dependency org.springframework.boot:spring-boot-starter-parent to v3.5.5 ([#929](https://github.com/madlinux768/retail-store-sample-app/issues/929)) ([72fa4e8](https://github.com/madlinux768/retail-store-sample-app/commit/72fa4e8f15253cce61c15657d0a396d3c95d5b50))
* **deps:** update dependency org.springframework.cloud:spring-cloud-gateway-webflux to v4.2.1 ([#798](https://github.com/madlinux768/retail-store-sample-app/issues/798)) ([0506dac](https://github.com/madlinux768/retail-store-sample-app/commit/0506dac93cb109d12665c418b3412db3d2eca53b))
* **deps:** update dependency reflect-metadata to ^0.2.0 ([#813](https://github.com/madlinux768/retail-store-sample-app/issues/813)) ([4b67fc5](https://github.com/madlinux768/retail-store-sample-app/commit/4b67fc57514596585c7d4aa5d75042f6a6dd95ba))
* **deps:** update dependency rxjs to v7.8.2 ([#772](https://github.com/madlinux768/retail-store-sample-app/issues/772)) ([04d1b3c](https://github.com/madlinux768/retail-store-sample-app/commit/04d1b3c3a7e0a75252ec26d99c5ca488e84b7fbe))
* **deps:** update dependency software.amazon.awssdk:bom to v2.31.5 ([#793](https://github.com/madlinux768/retail-store-sample-app/issues/793)) ([83365cb](https://github.com/madlinux768/retail-store-sample-app/commit/83365cb236b055a61d559896e27ffec7478e7169))
* **deps:** update dependency software.amazon.awssdk:bom to v2.31.6 ([#815](https://github.com/madlinux768/retail-store-sample-app/issues/815)) ([40f9e98](https://github.com/madlinux768/retail-store-sample-app/commit/40f9e98af9395dabb2278f5f6f246caa7cf5b413))
* **deps:** update dependency software.amazon.awssdk:bom to v2.31.75 ([#852](https://github.com/madlinux768/retail-store-sample-app/issues/852)) ([3229234](https://github.com/madlinux768/retail-store-sample-app/commit/32292347ae4b7ffd2172e4b17ef5210966527d64))
* **deps:** update dependency software.amazon.awssdk:bom to v2.31.76 ([#857](https://github.com/madlinux768/retail-store-sample-app/issues/857)) ([9565e5e](https://github.com/madlinux768/retail-store-sample-app/commit/9565e5e386c4c7e6863c1691c70d6f6151901152))
* **deps:** update dependency software.amazon.awssdk:bom to v2.32.13 ([#884](https://github.com/madlinux768/retail-store-sample-app/issues/884)) ([ebe9760](https://github.com/madlinux768/retail-store-sample-app/commit/ebe9760c6bda84e83dd38544384d30bc6d3ea9c9))
* **deps:** update kiota to v1.8.10 ([#930](https://github.com/madlinux768/retail-store-sample-app/issues/930)) ([a1012bf](https://github.com/madlinux768/retail-store-sample-app/commit/a1012bf29c862c4e91acf4fbd2547e62af95132a))
* **deps:** update kiota to v1.8.7 ([#854](https://github.com/madlinux768/retail-store-sample-app/issues/854)) ([726ba0b](https://github.com/madlinux768/retail-store-sample-app/commit/726ba0b484fed0573aaf76b0c13ead590f24ebdd))
* **deps:** update kiota to v1.8.8 ([#885](https://github.com/madlinux768/retail-store-sample-app/issues/885)) ([393fb36](https://github.com/madlinux768/retail-store-sample-app/commit/393fb3697e3ca9dc67bb3d95b72e3e38b41f95b7))
* **deps:** update module github.com/gin-gonic/gin to v1.10.1 ([#855](https://github.com/madlinux768/retail-store-sample-app/issues/855)) ([e81b40e](https://github.com/madlinux768/retail-store-sample-app/commit/e81b40e88c1286c86f705b68f1b4b16995a24cd7))
* **deps:** update module gorm.io/plugin/opentelemetry to v0.1.12 ([#799](https://github.com/madlinux768/retail-store-sample-app/issues/799)) ([b04eb5f](https://github.com/madlinux768/retail-store-sample-app/commit/b04eb5f984ea6c408165e988f7f25c80da9d2b85))
* **deps:** update opentelemetry-go monorepo to v1.37.0 ([#819](https://github.com/madlinux768/retail-store-sample-app/issues/819)) ([5312383](https://github.com/madlinux768/retail-store-sample-app/commit/531238309930200fdd1dd58200619c91d56a7f6e))
* **ecr:** change region from us-east-1 to us-west-2 ([3b60bb6](https://github.com/madlinux768/retail-store-sample-app/commit/3b60bb639d81c6b1dd86784b6b4467d257cfda55))
* **ecs:** add non-interactive terraform and dynamic ECR URL ([86ce3c3](https://github.com/madlinux768/retail-store-sample-app/commit/86ce3c32b30d188c8377a7c81c358220872a81f7))
* **ecs:** add Service Connect with correct syntax ([8c5eca1](https://github.com/madlinux768/retail-store-sample-app/commit/8c5eca12586b13655ceea73d4a2b2743f6f0e6c1))
* **ecs:** correct terraform validation and providers ([36c3b27](https://github.com/madlinux768/retail-store-sample-app/commit/36c3b27d7d157cdbd48f7cc887fed5fe64a9b6e8))
* **ecs:** downgrade AWS provider and add documentation ([3b90234](https://github.com/madlinux768/retail-store-sample-app/commit/3b90234760d200d4989e2e4d7129a6670455feda))
* **ecs:** migrate to Fargate and add S3 backend ([17d4045](https://github.com/madlinux768/retail-store-sample-app/commit/17d404512b99dcecf16a58d35d15d73683ca096a))
* **ecs:** replace deprecated aws_region.name with id ([098bf1c](https://github.com/madlinux768/retail-store-sample-app/commit/098bf1c2d662195fcdac7f7a914bfa98136b50fc))
* **ecs:** upgrade terraform version to 1.9 ([6d45e54](https://github.com/madlinux768/retail-store-sample-app/commit/6d45e54e1953cad05cd921e7aaf88b2261dae4bd))
* Expose UI chat configuration in chart ([58597cc](https://github.com/madlinux768/retail-store-sample-app/commit/58597cc9206758f95cf50f6b37df02fa828059d1))
* Fix load generator not completing orders ([#915](https://github.com/madlinux768/retail-store-sample-app/issues/915)) ([c43a8bb](https://github.com/madlinux768/retail-store-sample-app/commit/c43a8bb753008b860b59c795622e3e327233c398))
* Improved CW Logging for ECS default deployment ([#921](https://github.com/madlinux768/retail-store-sample-app/issues/921)) ([eff0668](https://github.com/madlinux768/retail-store-sample-app/commit/eff06680c3639acda4d878a2f01d68216955be95))
* Pin VPC endpoints module for AppRunner TF ([02ad54e](https://github.com/madlinux768/retail-store-sample-app/commit/02ad54e5fdc77402aa6c686270d06b8efb163ccd))
* Remove catalog in-memory db logging ([#880](https://github.com/madlinux768/retail-store-sample-app/issues/880)) ([83ca5dd](https://github.com/madlinux768/retail-store-sample-app/commit/83ca5dd7f7c30c4b752d9feca12f14a18b93f231))
* Revert Spring AI to 1.0.0 ([0a9994b](https://github.com/madlinux768/retail-store-sample-app/commit/0a9994b447e0e5e44c092eb0d5b4940bbe829e62))
* safely remove cart items ([#752](https://github.com/madlinux768/retail-store-sample-app/issues/752)) ([c766bd3](https://github.com/madlinux768/retail-store-sample-app/commit/c766bd3a9f2b24395f3a1276e0a1bc9fc7804f0d))
* **terraform:** add missing IAM permissions for GitHub Actions and ECS infrastructure ([506ed58](https://github.com/madlinux768/retail-store-sample-app/commit/506ed58ddc1dafa25f01ad2f56071d0ed032fa5c))
* **terraform:** correct auto-delete tag from yes to no in ECS managed instances ([95018ea](https://github.com/madlinux768/retail-store-sample-app/commit/95018eab47718a4d8b0fb363b03b4c17f49deec7))
* UI chart should only set theme if configured ([88ec5cd](https://github.com/madlinux768/retail-store-sample-app/commit/88ec5cd95722d5e164ddafdc1eb230d233667c4f))
* UI mock catalog tag filters ([114b3c9](https://github.com/madlinux768/retail-store-sample-app/commit/114b3c9584c7ac49be19868ce33e2c51b5f17916))
* Use correct RabbitMQ credential field names ([#911](https://github.com/madlinux768/retail-store-sample-app/issues/911)) ([2bbedc1](https://github.com/madlinux768/retail-store-sample-app/commit/2bbedc12863ec36bec65598d6f64b259530517f9))
* wait for VPC resource controller before deploying workloads ([#914](https://github.com/madlinux768/retail-store-sample-app/issues/914)) ([902302a](https://github.com/madlinux768/retail-store-sample-app/commit/902302a84aa52f9a0a84f8b807d7918deccee6d4))

## [1.3.0](https://github.com/aws-containers/retail-store-sample-app/compare/v1.2.4...v1.3.0) (2025-09-16)


### Features

* Add EventBridge lifecycle events for ECS Container Insights and update ADOT to CloudWatch Agent ([#913](https://github.com/aws-containers/retail-store-sample-app/issues/913)) ([549594b](https://github.com/aws-containers/retail-store-sample-app/commit/549594bf1f47d16f19a02ce040b55e4353dd8be6))


### Bug Fixes

* Add UI teal theme color ([#923](https://github.com/aws-containers/retail-store-sample-app/issues/923)) ([b382620](https://github.com/aws-containers/retail-store-sample-app/commit/b382620fcc7753b0e9c5256e972bc0844e8d9039))
* **deps:** update dependency org.openapitools:jackson-databind-nullable to v0.2.7 ([#926](https://github.com/aws-containers/retail-store-sample-app/issues/926)) ([46849a7](https://github.com/aws-containers/retail-store-sample-app/commit/46849a74089f06acad31222b6c4d7cdb8da32984))
* **deps:** update dependency org.projectlombok:lombok to v1.18.40 ([#927](https://github.com/aws-containers/retail-store-sample-app/issues/927)) ([4544834](https://github.com/aws-containers/retail-store-sample-app/commit/454483476947cc4e911f707969fdb898b4e9ae62))
* **deps:** update dependency org.springframework.ai:spring-ai-bom to v1.0.2 ([#928](https://github.com/aws-containers/retail-store-sample-app/issues/928)) ([948ce82](https://github.com/aws-containers/retail-store-sample-app/commit/948ce82b2192135ca5c69bb4582011f176dbda1b))
* **deps:** update dependency org.springframework.boot:spring-boot-starter-parent to v3.5.5 ([#929](https://github.com/aws-containers/retail-store-sample-app/issues/929)) ([72fa4e8](https://github.com/aws-containers/retail-store-sample-app/commit/72fa4e8f15253cce61c15657d0a396d3c95d5b50))
* **deps:** update kiota to v1.8.10 ([#930](https://github.com/aws-containers/retail-store-sample-app/issues/930)) ([a1012bf](https://github.com/aws-containers/retail-store-sample-app/commit/a1012bf29c862c4e91acf4fbd2547e62af95132a))
* Improved CW Logging for ECS default deployment ([#921](https://github.com/aws-containers/retail-store-sample-app/issues/921)) ([eff0668](https://github.com/aws-containers/retail-store-sample-app/commit/eff06680c3639acda4d878a2f01d68216955be95))
* Revert Spring AI to 1.0.0 ([0a9994b](https://github.com/aws-containers/retail-store-sample-app/commit/0a9994b447e0e5e44c092eb0d5b4940bbe829e62))
* wait for VPC resource controller before deploying workloads ([#914](https://github.com/aws-containers/retail-store-sample-app/issues/914)) ([902302a](https://github.com/aws-containers/retail-store-sample-app/commit/902302a84aa52f9a0a84f8b807d7918deccee6d4))

## [1.2.4](https://github.com/aws-containers/retail-store-sample-app/compare/v1.2.3...v1.2.4) (2025-08-13)


### Bug Fixes

* Fix load generator not completing orders ([#915](https://github.com/aws-containers/retail-store-sample-app/issues/915)) ([c43a8bb](https://github.com/aws-containers/retail-store-sample-app/commit/c43a8bb753008b860b59c795622e3e327233c398))

## [1.2.3](https://github.com/aws-containers/retail-store-sample-app/compare/v1.2.2...v1.2.3) (2025-08-01)


### Bug Fixes

* Consistent OpenTelemetry versions in Java components ([5ea06b9](https://github.com/aws-containers/retail-store-sample-app/commit/5ea06b9900d2d4878f560673c3664cb1386d7fb9))
* **deps:** update dependency software.amazon.awssdk:bom to v2.32.13 ([#884](https://github.com/aws-containers/retail-store-sample-app/issues/884)) ([ebe9760](https://github.com/aws-containers/retail-store-sample-app/commit/ebe9760c6bda84e83dd38544384d30bc6d3ea9c9))
* **deps:** update kiota to v1.8.8 ([#885](https://github.com/aws-containers/retail-store-sample-app/issues/885)) ([393fb36](https://github.com/aws-containers/retail-store-sample-app/commit/393fb3697e3ca9dc67bb3d95b72e3e38b41f95b7))
* Use correct RabbitMQ credential field names ([#911](https://github.com/aws-containers/retail-store-sample-app/issues/911)) ([2bbedc1](https://github.com/aws-containers/retail-store-sample-app/commit/2bbedc12863ec36bec65598d6f64b259530517f9))

## [1.2.2](https://github.com/aws-containers/retail-store-sample-app/compare/v1.2.1...v1.2.2) (2025-07-14)


### Bug Fixes

* **deps:** update dependency axios to v1.10.0 ([#874](https://github.com/aws-containers/retail-store-sample-app/issues/874)) ([4c0113e](https://github.com/aws-containers/retail-store-sample-app/commit/4c0113e8144252a068b199a7c00c0924ac52fb90))
* **deps:** update dependency org.springframework.boot:spring-boot-starter-parent to v3.5.3 ([#879](https://github.com/aws-containers/retail-store-sample-app/issues/879)) ([08120b1](https://github.com/aws-containers/retail-store-sample-app/commit/08120b10d311d5b30bbf3b30f7a80537ec61b912))
* Remove catalog in-memory db logging ([#880](https://github.com/aws-containers/retail-store-sample-app/issues/880)) ([83ca5dd](https://github.com/aws-containers/retail-store-sample-app/commit/83ca5dd7f7c30c4b752d9feca12f14a18b93f231))

## [1.2.1](https://github.com/aws-containers/retail-store-sample-app/compare/v1.2.0...v1.2.1) (2025-07-03)


### Bug Fixes

* **deps:** update dependency software.amazon.awssdk:bom to v2.31.76 ([#857](https://github.com/aws-containers/retail-store-sample-app/issues/857)) ([9565e5e](https://github.com/aws-containers/retail-store-sample-app/commit/9565e5e386c4c7e6863c1691c70d6f6151901152))
* **deps:** update kiota to v1.8.7 ([#854](https://github.com/aws-containers/retail-store-sample-app/issues/854)) ([726ba0b](https://github.com/aws-containers/retail-store-sample-app/commit/726ba0b484fed0573aaf76b0c13ead590f24ebdd))
* **deps:** update module github.com/gin-gonic/gin to v1.10.1 ([#855](https://github.com/aws-containers/retail-store-sample-app/issues/855)) ([e81b40e](https://github.com/aws-containers/retail-store-sample-app/commit/e81b40e88c1286c86f705b68f1b4b16995a24cd7))
* **deps:** update opentelemetry-go monorepo to v1.37.0 ([#819](https://github.com/aws-containers/retail-store-sample-app/issues/819)) ([5312383](https://github.com/aws-containers/retail-store-sample-app/commit/531238309930200fdd1dd58200619c91d56a7f6e))
* UI mock catalog tag filters ([114b3c9](https://github.com/aws-containers/retail-store-sample-app/commit/114b3c9584c7ac49be19868ce33e2c51b5f17916))

## [1.2.0](https://github.com/aws-containers/retail-store-sample-app/compare/v1.1.0...v1.2.0) (2025-07-02)


### Features

* Allow serving sample images from filesystem ([#853](https://github.com/aws-containers/retail-store-sample-app/issues/853)) ([43f3283](https://github.com/aws-containers/retail-store-sample-app/commit/43f3283f84ad0db99f75fa05e7eb7130c56d149e))
* Optimize asset image sizes ([#840](https://github.com/aws-containers/retail-store-sample-app/issues/840)) ([65a7748](https://github.com/aws-containers/retail-store-sample-app/commit/65a7748dfd99a1392baf788d2a059228a35062ce))
* Upgraded checkout to NestJS v11 ([#842](https://github.com/aws-containers/retail-store-sample-app/issues/842)) ([4f1c921](https://github.com/aws-containers/retail-store-sample-app/commit/4f1c921320061e6e7716a14409fa3c640c98a917))


### Bug Fixes

* **deps:** bump golang.org/x/crypto in /src/catalog ([#829](https://github.com/aws-containers/retail-store-sample-app/issues/829)) ([50ff85c](https://github.com/aws-containers/retail-store-sample-app/commit/50ff85c654aa7f4c4469d8fb27a28c2c96988214))
* **deps:** bump golang.org/x/net from 0.34.0 to 0.38.0 in /src/catalog ([#831](https://github.com/aws-containers/retail-store-sample-app/issues/831)) ([6303846](https://github.com/aws-containers/retail-store-sample-app/commit/63038463f862f2d18518c17b72355f53cf5b173c))
* **deps:** update dependency io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom to v2.17.0 ([#811](https://github.com/aws-containers/retail-store-sample-app/issues/811)) ([7ee50f7](https://github.com/aws-containers/retail-store-sample-app/commit/7ee50f71c86fe8bf27f5b7d3651e44d59c11086a))
* **deps:** update dependency io.swagger:swagger-annotations to v1.6.16 ([#849](https://github.com/aws-containers/retail-store-sample-app/issues/849)) ([17b44b6](https://github.com/aws-containers/retail-store-sample-app/commit/17b44b655bdd8011bc65d38301b720588042ead2))
* **deps:** update dependency org.projectlombok:lombok to v1.18.38 ([#850](https://github.com/aws-containers/retail-store-sample-app/issues/850)) ([2f76853](https://github.com/aws-containers/retail-store-sample-app/commit/2f768538e9ad409dba0ae4b1b83f76e3b0aed8b0))
* **deps:** update dependency org.springdoc:springdoc-openapi-starter-webmvc-ui to v2.8.9 ([#851](https://github.com/aws-containers/retail-store-sample-app/issues/851)) ([4a1a201](https://github.com/aws-containers/retail-store-sample-app/commit/4a1a2014222dd549850352f78851646830693143))
* **deps:** update dependency software.amazon.awssdk:bom to v2.31.75 ([#852](https://github.com/aws-containers/retail-store-sample-app/issues/852)) ([3229234](https://github.com/aws-containers/retail-store-sample-app/commit/32292347ae4b7ffd2172e4b17ef5210966527d64))
* UI chart should only set theme if configured ([88ec5cd](https://github.com/aws-containers/retail-store-sample-app/commit/88ec5cd95722d5e164ddafdc1eb230d233667c4f))

## [1.1.0](https://github.com/aws-containers/retail-store-sample-app/compare/v1.0.2...v1.1.0) (2025-03-23)


### Features

* Chaos testing endpoints ([#818](https://github.com/aws-containers/retail-store-sample-app/issues/818)) ([f8f2207](https://github.com/aws-containers/retail-store-sample-app/commit/f8f22078ea67049144bc2d59efc7a60c730c67f0))


### Bug Fixes

* **deps:** update dependency axios to v1.8.4 ([#791](https://github.com/aws-containers/retail-store-sample-app/issues/791)) ([06fe506](https://github.com/aws-containers/retail-store-sample-app/commit/06fe506a860bdadbe7fa69251b87ff62878f7f5d))
* **deps:** update dependency de.codecentric:chaos-monkey-spring-boot to v3.1.4 ([#769](https://github.com/aws-containers/retail-store-sample-app/issues/769)) ([8aeeea4](https://github.com/aws-containers/retail-store-sample-app/commit/8aeeea4ec3bbd6ec93c3a13aea43d15d805c0c3c))
* **deps:** update dependency de.codecentric:chaos-monkey-spring-boot to v3.2.0 ([#810](https://github.com/aws-containers/retail-store-sample-app/issues/810)) ([aff5aa9](https://github.com/aws-containers/retail-store-sample-app/commit/aff5aa94a81923765d38f3a4dd7b639706be1563))
* **deps:** update dependency org.springframework.boot:spring-boot-starter-parent to v3.4.4 ([#802](https://github.com/aws-containers/retail-store-sample-app/issues/802)) ([3a9b53f](https://github.com/aws-containers/retail-store-sample-app/commit/3a9b53f1a1387ea0bfeabd7d6495983f15922ac3))
* **deps:** update dependency org.springframework.cloud:spring-cloud-gateway-webflux to v4.2.1 ([#798](https://github.com/aws-containers/retail-store-sample-app/issues/798)) ([0506dac](https://github.com/aws-containers/retail-store-sample-app/commit/0506dac93cb109d12665c418b3412db3d2eca53b))
* **deps:** update dependency reflect-metadata to ^0.2.0 ([#813](https://github.com/aws-containers/retail-store-sample-app/issues/813)) ([4b67fc5](https://github.com/aws-containers/retail-store-sample-app/commit/4b67fc57514596585c7d4aa5d75042f6a6dd95ba))
* **deps:** update dependency rxjs to v7.8.2 ([#772](https://github.com/aws-containers/retail-store-sample-app/issues/772)) ([04d1b3c](https://github.com/aws-containers/retail-store-sample-app/commit/04d1b3c3a7e0a75252ec26d99c5ca488e84b7fbe))
* **deps:** update dependency software.amazon.awssdk:bom to v2.31.5 ([#793](https://github.com/aws-containers/retail-store-sample-app/issues/793)) ([83365cb](https://github.com/aws-containers/retail-store-sample-app/commit/83365cb236b055a61d559896e27ffec7478e7169))
* **deps:** update dependency software.amazon.awssdk:bom to v2.31.6 ([#815](https://github.com/aws-containers/retail-store-sample-app/issues/815)) ([40f9e98](https://github.com/aws-containers/retail-store-sample-app/commit/40f9e98af9395dabb2278f5f6f246caa7cf5b413))
* **deps:** update module gorm.io/plugin/opentelemetry to v0.1.12 ([#799](https://github.com/aws-containers/retail-store-sample-app/issues/799)) ([b04eb5f](https://github.com/aws-containers/retail-store-sample-app/commit/b04eb5f984ea6c408165e988f7f25c80da9d2b85))

## [1.0.2](https://github.com/aws-containers/retail-store-sample-app/compare/v1.0.1...v1.0.2) (2025-03-20)


### Bug Fixes

* Expose UI chat configuration in chart ([58597cc](https://github.com/aws-containers/retail-store-sample-app/commit/58597cc9206758f95cf50f6b37df02fa828059d1))

## [1.0.1](https://github.com/aws-containers/retail-store-sample-app/compare/v1.0.0...v1.0.1) (2025-03-13)


### Bug Fixes

* safely remove cart items ([#752](https://github.com/aws-containers/retail-store-sample-app/issues/752)) ([c766bd3](https://github.com/aws-containers/retail-store-sample-app/commit/c766bd3a9f2b24395f3a1276e0a1bc9fc7804f0d))

## 1.0.0 (2025-02-28)


### Features

* Add headers, panic, echo and store utilities ([#728](https://github.com/aws-containers/retail-store-sample-app/issues/728)) ([c4f703b](https://github.com/aws-containers/retail-store-sample-app/commit/c4f703bc78bd832116a78e78bf44024aa5c361ca))
* Application v1 ([#742](https://github.com/aws-containers/retail-store-sample-app/issues/742)) ([2ea99fb](https://github.com/aws-containers/retail-store-sample-app/commit/2ea99fbf94c891c4da166c2527f082ab5c621240))
