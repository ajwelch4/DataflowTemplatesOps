/**
 * Copyright 2022 Google. This software is provided as-is, without warranty or
 * representation for any use or purpose. Your use of it is subject to your
 * agreement with Google.
 */
package com.google.cloud.teleport.v2.ops.jdbctobigqueryplus;

import com.hashicorp.cdktf.providers.google_beta.google_sql_database.GoogleSqlDatabase;
import com.hashicorp.cdktf.providers.google_beta.google_sql_database_instance.GoogleSqlDatabaseInstance;
import com.hashicorp.cdktf.providers.google_beta.google_sql_database_instance.GoogleSqlDatabaseInstanceSettings;
import com.hashicorp.cdktf.providers.google_beta.google_sql_user.GoogleSqlUser;
import software.constructs.Construct;

/** Cloud SQL resources. */
public class CloudSql extends Construct {

  private GoogleSqlDatabaseInstance sourcePostgresInstance;
  private GoogleSqlDatabase sourcePostgresDatabase;
  private GoogleSqlUser sourcePostgresReadOnlyUser;
  private String sourcePostgresReadOnlyUserPassword;

  public CloudSql(
      Construct scope,
      String id,
      String project,
      String region,
      String sourcePostgresReadOnlyUserPassword,
      String sourcePostgresAdminUserPassword) {
    super(scope, id);

    this.sourcePostgresReadOnlyUserPassword = sourcePostgresReadOnlyUserPassword;

    sourcePostgresInstance =
        GoogleSqlDatabaseInstance.Builder.create(this, "source_postgres_instance")
            .project(project)
            .name("source-postgres-instance")
            .databaseVersion("POSTGRES_11")
            .region(region)
            .settings(GoogleSqlDatabaseInstanceSettings.builder().tier("db-f1-micro").build())
            .deletionProtection(false)
            .build();

    sourcePostgresDatabase =
        GoogleSqlDatabase.Builder.create(this, "source_postgres_database")
            .project(project)
            .name("foo")
            .instance(sourcePostgresInstance.getName())
            .build();

    sourcePostgresReadOnlyUser =
        GoogleSqlUser.Builder.create(this, "source_postgres_read_only_user")
            .project(project)
            .name("read_only_user")
            .instance(sourcePostgresInstance.getName())
            .password(sourcePostgresReadOnlyUserPassword)
            .build();

    GoogleSqlUser.Builder.create(this, "source_postgres_admin_user")
        .project(project)
        .name("admin_user")
        .instance(sourcePostgresInstance.getName())
        .password(sourcePostgresAdminUserPassword)
        .build();
  }

  public String getSourcePostgresReadOnlyUserName() {
    return sourcePostgresReadOnlyUser.getName();
  }

  public String getSourcePostgresReadOnlyUserPassword() {
    return sourcePostgresReadOnlyUserPassword;
  }

  public GoogleSqlDatabaseInstance getSourcePostgresInstance() {
    return sourcePostgresInstance;
  }

  public GoogleSqlDatabase getSourcePostgresDatabase() {
    return sourcePostgresDatabase;
  }
}
