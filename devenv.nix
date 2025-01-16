{ pkgs, lib, config, inputs, ... }:

{
  packages = [ 
    pkgs.git
    pkgs.jq
    pkgs.yq
    pkgs.pre-commit
    pkgs.kubectl
    pkgs.awscli
    pkgs.yarn
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
  languages.go = {
    enable = true;
    package = pkgs.go_1_22;
  };
  languages.terraform.enable = true;
}