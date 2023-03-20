/**
 * Copyright 2022 Google. This software is provided as-is, without warranty or
 * representation for any use or purpose. Your use of it is subject to your
 * agreement with Google.
 */
package com.google.cloud.pso.pipelines.ops;

import com.hashicorp.cdktf.providers.google_beta.google_project_iam_member.GoogleProjectIamMember;
import com.hashicorp.cdktf.providers.google_beta.google_service_account.GoogleServiceAccount;
import software.constructs.Construct;

/** Service Account resources. */
public class ServiceAccount extends Construct {

  private GoogleServiceAccount dataflowWorker;

  public ServiceAccount(Construct scope, String id, String project) {
    super(scope, id);

    dataflowWorker =
        GoogleServiceAccount.Builder.create(this, "dataflow_worker")
            .project(project)
            .accountId("dataflow-worker-sa")
            .displayName("Dataflow Worker Service Account.")
            .build();

    GoogleProjectIamMember.Builder.create(this, "dataflow_worker_role")
        .project(project)
        .role("roles/dataflow.worker")
        .member("serviceAccount:" + dataflowWorker.getEmail())
        .build();

    // Required to access Flex Template.
    GoogleProjectIamMember.Builder.create(this, "dataflow_worker_storage_object_viewer_role")
        .project(project)
        .role("roles/storage.objectViewer")
        .member("serviceAccount:" + dataflowWorker.getEmail())
        .build();

    GoogleProjectIamMember.Builder.create(this, "dataflow_worker_bigquery_data_editor_role")
        .project(project)
        .role("roles/bigquery.dataEditor")
        .member("serviceAccount:" + dataflowWorker.getEmail())
        .build();

    GoogleProjectIamMember.Builder.create(this, "dataflow_worker_bigquery_job_user_role")
        .project(project)
        .role("roles/bigquery.jobUser")
        .member("serviceAccount:" + dataflowWorker.getEmail())
        .build();

    GoogleProjectIamMember.Builder.create(this, "dataflow_worker_bigquery_read_session_user_role")
        .project(project)
        .role("roles/bigquery.readSessionUser")
        .member("serviceAccount:" + dataflowWorker.getEmail())
        .build();
  }

  public GoogleServiceAccount getDataflowWorker() {
    return dataflowWorker;
  }
}
