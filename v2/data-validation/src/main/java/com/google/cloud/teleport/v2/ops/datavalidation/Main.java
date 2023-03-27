/**
 * Copyright 2022 Google. This software is provided as-is, without warranty or
 * representation for any use or purpose. Your use of it is subject to your
 * agreement with Google.
 */
package com.google.cloud.teleport.v2.ops.datavalidation;

import com.hashicorp.cdktf.App;

/** Main class defining pipelines infrastructure. */
public class Main {
  public static void main(String[] args) {
    final App app = new App();
    new PipelinesStack(
        app, "pipelines_prod", "production", System.getenv("PROJECT_ID"), System.getenv("REGION"));
    app.synth();
  }
}
