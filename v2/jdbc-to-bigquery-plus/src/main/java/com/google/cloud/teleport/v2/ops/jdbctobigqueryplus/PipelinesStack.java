/**
 * Copyright 2022 Google. This software is provided as-is, without warranty or
 * representation for any use or purpose. Your use of it is subject to your
 * agreement with Google.
 */
package com.google.cloud.teleport.v2.ops.jdbctobigqueryplus;

import com.hashicorp.cdktf.TerraformOutput;
import com.hashicorp.cdktf.TerraformStack;
import com.hashicorp.cdktf.TerraformVariable;
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

    TerraformVariable sourcePostgresReadOnlyUserPassword =
        TerraformVariable.Builder.create(this, "source_postgres_read_only_user_password")
            .type("string")
            .sensitive(true)
            .description("Source Postgres read only user password.")
            .build();

    TerraformVariable sourcePostgresAdminUserPassword =
        TerraformVariable.Builder.create(this, "source_postgres_admin_user_password")
            .type("string")
            .sensitive(true)
            .description("Source Postgres admin user password.")
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

    CloudSql cloudSql =
        new CloudSql(
            this,
            "cloud_sql",
            project,
            region,
            sourcePostgresReadOnlyUserPassword.getStringValue(),
            sourcePostgresAdminUserPassword.getStringValue());

    BigQuery bigQuery =
        new BigQuery(this, "bigquery", project, region, serviceAccount.getDataflowWorker());

    SecretManager secretManager =
        new SecretManager(
            this,
            "secret_manager",
            project,
            serviceApi.getSecretManagerApi(),
            serviceAccount.getDataflowWorker(),
            cloudSql);

    TerraformOutput.Builder.create(this, "dataflow_worker_service_account")
        .value(serviceAccount.getDataflowWorker().getEmail())
        .build();

    TerraformOutput.Builder.create(this, "dataflow_pipeline_config_bucket")
        .value(storage.getDataflowPipelineConfigBucket().getName())
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

    TerraformOutput.Builder.create(this, "cloud_sql_source_postgres_jdbc_url_secret_id")
        .value(secretManager.getSourcePostgresJdbcUrlSecretId())
        .build();

    TerraformOutput.Builder.create(this, "cloud_sql_source_postgres_instance")
        .value(cloudSql.getSourcePostgresInstance().getName())
        .build();

    TerraformOutput.Builder.create(this, "cloud_sql_source_postgres_database")
        .value(cloudSql.getSourcePostgresDatabase().getName())
        .build();

    TerraformOutput.Builder.create(this, "cloud_sql_source_postgres_connection_name")
        .value(cloudSql.getSourcePostgresInstance().getConnectionName())
        .build();
  }
}
