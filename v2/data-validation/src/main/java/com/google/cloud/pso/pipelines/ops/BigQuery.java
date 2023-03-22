/**
 * Copyright 2022 Google. This software is provided as-is, without warranty or
 * representation for any use or purpose. Your use of it is subject to your
 * agreement with Google.
 */
package com.google.cloud.pso.pipelines.ops;

import com.hashicorp.cdktf.providers.google_beta.google_bigquery_dataset.GoogleBigqueryDataset;
import com.hashicorp.cdktf.providers.google_beta.google_bigquery_table.GoogleBigqueryTable;
import com.hashicorp.cdktf.providers.google_beta.google_service_account.GoogleServiceAccount;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import software.constructs.Construct;

/** BigQuery resources. */
public class BigQuery extends Construct {

  private GoogleBigqueryDataset simbaJdbcDataset;
  private GoogleBigqueryDataset loadTestDataset;
  private GoogleBigqueryTable loadTestMetricsTable;
  private GoogleBigqueryTable loadTestResultsTable;

  public BigQuery(
      Construct scope,
      String id,
      String project,
      String region,
      GoogleServiceAccount dataflowWorkerServiceAccount) {
    super(scope, id);

    simbaJdbcDataset =
        GoogleBigqueryDataset.Builder.create(this, "simba_jdbc_bigquery_dataset")
            .project(project)
            .location(region)
            .datasetId("_simba_jdbc")
            .deleteContentsOnDestroy(true)
            .build();

    loadTestDataset =
        GoogleBigqueryDataset.Builder.create(this, "load_test_bigquery_dataset")
            .project(project)
            .location(region)
            .datasetId("load_test")
            .deleteContentsOnDestroy(true)
            .build();

    loadTestMetricsTable =
        GoogleBigqueryTable.Builder.create(this, "load_test_metrics")
            .project(loadTestDataset.getProject())
            .datasetId(loadTestDataset.getDatasetId())
            .tableId("metrics")
            .schema(readBigQueryJsonSchemaFile("load_test_metrics.json"))
            .deletionProtection(false)
            .build();

    loadTestResultsTable =
        GoogleBigqueryTable.Builder.create(this, "load_test_results")
            .project(loadTestDataset.getProject())
            .datasetId(loadTestDataset.getDatasetId())
            .tableId("results")
            .schema(readBigQueryJsonSchemaFile("load_test_results.json"))
            .deletionProtection(false)
            .build();
  }

  public GoogleBigqueryDataset getSimbaJdbcDataset() {
    return simbaJdbcDataset;
  }

  public GoogleBigqueryDataset getLoadTestDataset() {
    return loadTestDataset;
  }

  public GoogleBigqueryTable getLoadTestMetricsTable() {
    return loadTestMetricsTable;
  }

  public GoogleBigqueryTable getLoadTestResultsTable() {
    return loadTestResultsTable;
  }

  private String readBigQueryJsonSchemaFile(String fileName) {
    return new BufferedReader(
            new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(fileName), StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));
  }
}
