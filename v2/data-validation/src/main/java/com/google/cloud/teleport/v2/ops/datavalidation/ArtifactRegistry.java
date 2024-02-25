/**
 * Copyright 2022 Google. This software is provided as-is, without warranty or
 * representation for any use or purpose. Your use of it is subject to your
 * agreement with Google.
 */
package com.google.cloud.teleport.v2.ops.datavalidation;

import com.hashicorp.cdktf.providers.google_beta.google_artifact_registry_repository.GoogleArtifactRegistryRepository;
import com.hashicorp.cdktf.providers.google_beta.google_artifact_registry_repository_iam_member.GoogleArtifactRegistryRepositoryIamMember;
import com.hashicorp.cdktf.providers.google_beta.google_project_service.GoogleProjectService;
import com.hashicorp.cdktf.providers.google_beta.google_service_account.GoogleServiceAccount;
import java.util.Arrays;
import software.constructs.Construct;

public class ArtifactRegistry extends Construct {
  private GoogleArtifactRegistryRepository dataflowFlexTemplateRegistryRepository;

  public ArtifactRegistry(
      Construct scope,
      String id,
      String project,
      String region,
      GoogleProjectService artifactRegistryApi,
      GoogleServiceAccount dataflowWorkerServiceAccount) {
    super(scope, id);

    dataflowFlexTemplateRegistryRepository =
        GoogleArtifactRegistryRepository.Builder.create(
                this, "dataflow_flex_template_registry_repository")
            .dependsOn(Arrays.asList(artifactRegistryApi))
            .project(project)
            .location(region)
            .repositoryId("data-validation")
            .format("docker")
            .build();

    GoogleArtifactRegistryRepositoryIamMember.Builder.create(
            this, "dataflow_worker_artifact_registry_reader_role")
        .project(project)
        .location(region)
        .repository(getDataflowFlexTemplateRegistryRepository().getRepositoryId())
        .role("roles/artifactregistry.reader")
        .member("serviceAccount:" + dataflowWorkerServiceAccount.getEmail())
        .build();
  }

  public GoogleArtifactRegistryRepository getDataflowFlexTemplateRegistryRepository() {
    return dataflowFlexTemplateRegistryRepository;
  }
}
