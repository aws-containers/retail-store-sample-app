{ pkgs, lib, config, inputs, ... }:

{
  packages = [ 
    pkgs.jq
    pkgs.yq
    pkgs.pre-commit
    pkgs.kubectl
    pkgs.kind
    pkgs.kubernetes-helm
    pkgs.kubernetes-helmPlugins.helm-diff
    pkgs.helmfile
    pkgs.tilt
    pkgs.awscli
    pkgs.yarn
    pkgs.delve
  ];

  languages.javascript = {
    enable = true;
    package = pkgs.nodejs_20;
  };
  languages.typescript = {
    enable = true;
  };
  languages.java = {
    enable = true;
    jdk.package = pkgs.jdk21_headless;
    maven.enable = true;
  };
  languages.terraform.enable = true;

  devcontainer.enable = true;

  git-hooks.hooks = {
    prettier.enable = true;
    terraform-format.enable = true;
    tflint.enable = true;
    gofmt.enable = true;
  };

  hardeningDisable = ["fortify"];
}