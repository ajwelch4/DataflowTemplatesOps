/**
 * Copyright 2022 Google. This software is provided as-is, without warranty or
 * representation for any use or purpose. Your use of it is subject to your
 * agreement with Google.
 */
package com.google.cloud.teleport.v2.ops.datavalidation;

import com.hashicorp.cdktf.providers.google_beta.google_service_account.GoogleServiceAccount;
import com.hashicorp.cdktf.providers.google_beta.google_storage_bucket.GoogleStorageBucket;
import com.hashicorp.cdktf.providers.google_beta.google_storage_bucket_iam_member.GoogleStorageBucketIamMember;
import com.hashicorp.cdktf.providers.google_beta.google_storage_bucket_object.GoogleStorageBucketObject;
import software.constructs.Construct;

/** Storage resources. */
public class Storage extends Construct {

  private GoogleStorageBucket dataflowPipelineDataBucket;
  private GoogleStorageBucket dataflowFlexTemplateBucket;
  private GoogleStorageBucket dataflowGcpTempLocationBucket;

  public Storage(
      Construct scope,
      String id,
      String project,
      String region,
      GoogleServiceAccount dataflowWorkerServiceAccount) {
    super(scope, id);

    dataflowPipelineDataBucket =
        GoogleStorageBucket.Builder.create(this, "dataflow_pipeline_data_bucket")
            .project(project)
            .name(project + "-dataflow-pipeline-data")
            .location(region)
            .forceDestroy(true)
            .uniformBucketLevelAccess(true)
            .build();

    GoogleStorageBucketIamMember.Builder.create(
            this, "dataflow_worker_dataflow_pipeline_data_storage_admin_role")
        .bucket(dataflowPipelineDataBucket.getName())
        .role("roles/storage.admin")
        .member("serviceAccount:" + dataflowWorkerServiceAccount.getEmail())
        .build();

    dataflowFlexTemplateBucket =
        GoogleStorageBucket.Builder.create(this, "dataflow_flex_template_bucket")
            .project(project)
            .name(project + "-dataflow-flex-template")
            .location(region)
            .forceDestroy(true)
            .uniformBucketLevelAccess(true)
            .build();

    GoogleStorageBucketIamMember.Builder.create(
            this, "dataflow_worker_flex_template_storage_object_viewer_role")
        .bucket(dataflowFlexTemplateBucket.getName())
        .role("roles/storage.objectViewer")
        .member("serviceAccount:" + dataflowWorkerServiceAccount.getEmail())
        .build();

    dataflowGcpTempLocationBucket =
        GoogleStorageBucket.Builder.create(this, "dataflow_gcp_temp_location_bucket")
            .project(project)
            .name(project + "-dataflow-gcp-temp-location")
            .location(region)
            .forceDestroy(true)
            .uniformBucketLevelAccess(true)
            .build();

    GoogleStorageBucketIamMember.Builder.create(
            this, "dataflow_worker_gcp_temp_location_storage_admin_role")
        .bucket(dataflowGcpTempLocationBucket.getName())
        .role("roles/storage.admin")
        .member("serviceAccount:" + dataflowWorkerServiceAccount.getEmail())
        .build();
  }

  public GoogleStorageBucket getDataflowPipelineDataBucket() {
    return dataflowPipelineDataBucket;
  }

  public GoogleStorageBucket getDataflowFlexTemplateBucket() {
    return dataflowFlexTemplateBucket;
  }

  public GoogleStorageBucket getDataflowGcpTempLocationBucket() {
    return dataflowGcpTempLocationBucket;
  }
}
