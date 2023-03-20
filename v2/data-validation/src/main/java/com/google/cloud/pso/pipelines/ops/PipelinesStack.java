/**
 * Copyright 2022 Google. This software is provided as-is, without warranty or
 * representation for any use or purpose. Your use of it is subject to your
 * agreement with Google.
 */
package com.google.cloud.pso.pipelines.ops;

import com.hashicorp.cdktf.TerraformOutput;
import com.hashicorp.cdktf.TerraformStack;
import com.hashicorp.cdktf.providers.google_beta.provider.GoogleBetaProvider;
import software.constructs.Construct;

/** Pipelines stack. */
public class PipelinesStack extends TerraformStack {

  public PipelinesStack(
      Construct scope, String name, String environment, String project, String region) {
    super(scope, name);

    GoogleBetaProvider.Builder.create(this, "google_beta_provider")
        .region(region)
        .project(project)
        .build();

    ServiceApi serviceApi = new ServiceApi(this, "service_api", project);

    ServiceAccount serviceAccount = new ServiceAccount(this, "service_account", project);

    ContainerRegistry containerRegistry =
        new ContainerRegistry(
            this,
            "container_registry",
            project,
            region,
            serviceApi.getContainerRegistryApi(),
            serviceAccount.getDataflowWorker());

    Storage storage =
        new Storage(this, "storage", project, region, serviceAccount.getDataflowWorker());

    BigQuery bigQuery =
        new BigQuery(this, "bigquery", project, region, serviceAccount.getDataflowWorker());

    TerraformOutput.Builder.create(this, "dataflow_worker_service_account")
        .value(serviceAccount.getDataflowWorker().getEmail())
        .build();

    TerraformOutput.Builder.create(this, "dataflow_pipeline_data_bucket")
        .value(storage.getDataflowPipelineDataBucket().getName())
        .build();

    TerraformOutput.Builder.create(this, "dataflow_flex_template_container_registry")
        .value(containerRegistry.getDataflowFlexTemplateRegistry().getId())
        .build();

    TerraformOutput.Builder.create(this, "dataflow_flex_template_bucket")
        .value(storage.getDataflowFlexTemplateBucket().getName())
        .build();

    TerraformOutput.Builder.create(this, "dataflow_gcp_temp_location_bucket")
        .value(storage.getDataflowGcpTempLocationBucket().getName())
        .build();

    TerraformOutput.Builder.create(this, "bigquery_load_test_dataset")
        .value(bigQuery.getLoadTestDataset())
        .build();

    TerraformOutput.Builder.create(this, "bigquery_load_test_metrics_table")
        .value(bigQuery.getLoadTestMetricsTable())
        .build();

    TerraformOutput.Builder.create(this, "bigquery_load_test_results_table")
        .value(bigQuery.getLoadTestResultsTable())
        .build();
  }
}
