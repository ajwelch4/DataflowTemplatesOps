# JDBC to Bigquery + Ops

Install:

```shell
npm install
```

Export the following environment variables:

```shell
export PROJECT_ID=<YOUR_PROJECT_ID>
export REGION=<YOUR_DESIRED_REGION>
export TF_VAR_source_postgres_admin_user_password="49adf11bb6cdcc954cb4bab0dc03726957b6791e"
export TF_VAR_source_postgres_read_only_user_password="fd93a4bc7e9d471b758dcc12df56e056b4dc5334"
```

To view what will be deployed, run:

```shell
npx cdktf diff
```

To deploy the infrastructure, run:

```shell
npx cdktf deploy
```

To destroy the infrastructure, run:

```shell
npx cdktf destroy
```
