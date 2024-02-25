/**
 * Copyright 2022 Google. This software is provided as-is, without warranty or
 * representation for any use or purpose. Your use of it is subject to your
 * agreement with Google.
 */
package com.google.cloud.teleport.v2.ops.datavalidation;

import com.hashicorp.cdktf.providers.google_beta.google_project_service.GoogleProjectService;
import software.constructs.Construct;

/** Service API resources. */
public class ServiceApi extends Construct {

  private GoogleProjectService artifactRegistryApi;

  public ServiceApi(Construct scope, String id, String project) {
    super(scope, id);

    GoogleProjectService.Builder.create(this, "dataflow_api")
        .project(project)
        .service("dataflow.googleapis.com")
        .build();

    artifactRegistryApi =
        GoogleProjectService.Builder.create(this, "artifact_registry_api")
            .project(project)
            .service("artifactregistry.googleapis.com")
            .build();

    GoogleProjectService.Builder.create(this, "container_registry_api")
        .project(project)
        .service("containerregistry.googleapis.com")
        .build();
  }

  public GoogleProjectService getArtifactRegistryApi() {
    return artifactRegistryApi;
  }
}
