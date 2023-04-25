/**
 * Copyright 2022 Google. This software is provided as-is, without warranty or
 * representation for any use or purpose. Your use of it is subject to your
 * agreement with Google.
 */
package com.google.cloud.teleport.v2.ops.jdbctobigqueryplus;

import com.hashicorp.cdktf.providers.google_beta.google_container_registry.GoogleContainerRegistry;
import com.hashicorp.cdktf.providers.google_beta.google_project_service.GoogleProjectService;
import com.hashicorp.cdktf.providers.google_beta.google_service_account.GoogleServiceAccount;
import com.hashicorp.cdktf.providers.google_beta.google_storage_bucket_iam_member.GoogleStorageBucketIamMember;
import java.util.Arrays;
import software.constructs.Construct;

/** Container Registry resources. */
public class ContainerRegistry extends Construct {

  private GoogleContainerRegistry dataflowFlexTemplateRegistry;

  public ContainerRegistry(
      Construct scope,
      String id,
      String project,
      String region,
      GoogleProjectService containerRegistryApi,
      GoogleServiceAccount dataflowWorkerServiceAccount) {
    super(scope, id);

    dataflowFlexTemplateRegistry =
        GoogleContainerRegistry.Builder.create(this, "dataflow_flex_template_registry")
            .dependsOn(Arrays.asList(containerRegistryApi))
            .project(project)
            .build();

    GoogleStorageBucketIamMember.Builder.create(
            this, "dataflow_flex_template_gcr_storage_object_viewer_role")
        .bucket(dataflowFlexTemplateRegistry.getId())
        .role("roles/storage.objectViewer")
        .member("serviceAccount:" + dataflowWorkerServiceAccount.getEmail())
        .build();
  }

  public GoogleContainerRegistry getDataflowFlexTemplateRegistry() {
    return dataflowFlexTemplateRegistry;
  }
}
