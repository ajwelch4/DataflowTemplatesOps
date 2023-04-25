/**
 * Copyright 2022 Google. This software is provided as-is, without warranty or
 * representation for any use or purpose. Your use of it is subject to your
 * agreement with Google.
 */
package com.google.cloud.teleport.v2.ops.jdbctobigqueryplus;

import com.hashicorp.cdktf.providers.google_beta.google_bigquery_dataset.GoogleBigqueryDataset;
import com.hashicorp.cdktf.providers.google_beta.google_bigquery_dataset_iam_member.GoogleBigqueryDatasetIamMember;
import com.hashicorp.cdktf.providers.google_beta.google_bigquery_table.GoogleBigqueryTable;
import com.hashicorp.cdktf.providers.google_beta.google_service_account.GoogleServiceAccount;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import software.constructs.Construct;

/** Artifact Registry resources. */
public class BigQuery extends Construct {

  private GoogleBigqueryDataset dataset;

  public BigQuery(
      Construct scope,
      String id,
      String project,
      String region,
      GoogleServiceAccount dataflowWorkerServiceAccount) {
    super(scope, id);

    dataset =
        GoogleBigqueryDataset.Builder.create(this, "bigquery_dataset")
            .project(project)
            .location(region)
            .datasetId("foo_bar")
            .deleteContentsOnDestroy(true)
            .build();

    GoogleBigqueryTable.Builder.create(this, "customers")
        .project(dataset.getProject())
        .datasetId(dataset.getDatasetId())
        .tableId("customers")
        .schema(readBigQueryJsonSchemaFile("customers.json"))
        .deletionProtection(false)
        .build();

    GoogleBigqueryTable.Builder.create(this, "orders")
        .project(dataset.getProject())
        .datasetId(dataset.getDatasetId())
        .tableId("orders")
        .schema(readBigQueryJsonSchemaFile("orders.json"))
        .deletionProtection(false)
        .build();

    GoogleBigqueryDatasetIamMember.Builder.create(this, "dataflow_worker_dataset_editor_role")
        .project(dataset.getProject())
        .datasetId(dataset.getDatasetId())
        .role("roles/bigquery.dataEditor")
        .member("serviceAccount:" + dataflowWorkerServiceAccount.getEmail())
        .build();
  }

  public GoogleBigqueryDataset getDataset() {
    return dataset;
  }

  private String readBigQueryJsonSchemaFile(String fileName) {
    return new BufferedReader(
            new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(fileName), StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));
  }
}
