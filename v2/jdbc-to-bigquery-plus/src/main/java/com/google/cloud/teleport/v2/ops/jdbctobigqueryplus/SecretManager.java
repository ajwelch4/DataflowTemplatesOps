/**
 * Copyright 2022 Google. This software is provided as-is, without warranty or
 * representation for any use or purpose. Your use of it is subject to your
 * agreement with Google.
 */
package com.google.cloud.teleport.v2.ops.jdbctobigqueryplus;

import com.hashicorp.cdktf.providers.google_beta.google_project_service.GoogleProjectService;
import com.hashicorp.cdktf.providers.google_beta.google_secret_manager_secret.GoogleSecretManagerSecret;
import com.hashicorp.cdktf.providers.google_beta.google_secret_manager_secret.GoogleSecretManagerSecretReplication;
import com.hashicorp.cdktf.providers.google_beta.google_secret_manager_secret_iam_member.GoogleSecretManagerSecretIamMember;
import com.hashicorp.cdktf.providers.google_beta.google_secret_manager_secret_version.GoogleSecretManagerSecretVersion;
import com.hashicorp.cdktf.providers.google_beta.google_service_account.GoogleServiceAccount;
import java.util.Arrays;
import software.constructs.Construct;

/** Secret Manager resources. */
public class SecretManager extends Construct {

  private GoogleSecretManagerSecret sourcePostgresJdbcUrl;
  private GoogleSecretManagerSecretVersion sourcePostgresJdbcUrlSecret;

  public SecretManager(
      Construct scope,
      String id,
      String project,
      GoogleProjectService secretManagerApi,
      GoogleServiceAccount dataflowWorkerServiceAccount,
      CloudSql cloudSql) {
    super(scope, id);

    sourcePostgresJdbcUrl =
        GoogleSecretManagerSecret.Builder.create(this, "source-postgres-jdbc-url")
            .project(project)
            .secretId("source-postgres-jdbc-url")
            .replication(GoogleSecretManagerSecretReplication.builder().automatic(true).build())
            .dependsOn(Arrays.asList(secretManagerApi))
            .build();

    sourcePostgresJdbcUrlSecret =
        GoogleSecretManagerSecretVersion.Builder.create(this, "source-postgres-jdbc-url-secret")
            .secret(sourcePostgresJdbcUrl.getId())
            .secretData(
                "jdbc:postgresql:///"
                    + cloudSql.getSourcePostgresDatabase().getName()
                    + "?cloudSqlInstance="
                    + cloudSql.getSourcePostgresInstance().getConnectionName()
                    + "&socketFactory=com.google.cloud.sql.postgres.SocketFactory&user="
                    + cloudSql.getSourcePostgresReadOnlyUserName()
                    + "&password="
                    + cloudSql.getSourcePostgresReadOnlyUserPassword())
            .build();

    GoogleSecretManagerSecretIamMember.Builder.create(
            this, "-dataflow-worker-secretmanager-secret-accessor-role")
        .project(project)
        .secretId(sourcePostgresJdbcUrl.getSecretId())
        .role("roles/secretmanager.secretAccessor")
        .member("serviceAccount:" + dataflowWorkerServiceAccount.getEmail())
        .build();
  }

  public GoogleSecretManagerSecret getSourcePostgresJdbcUrl() {
    return sourcePostgresJdbcUrl;
  }

  public String getSourcePostgresJdbcUrlSecretId() {
    return sourcePostgresJdbcUrlSecret.getName();
  }
}
