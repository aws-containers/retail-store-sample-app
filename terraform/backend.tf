terraform {
  required_version = "1.14.8"

  cloud {

    organization = "NT548_"

    workspaces {
      name = "NT548"
    }
  }
}