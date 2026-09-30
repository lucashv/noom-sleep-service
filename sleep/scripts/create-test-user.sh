#!/usr/bin/env bash
set -euo pipefail

USER_ID="${USER_ID:-00000000-0000-4000-8000-000000000001}"
USERNAME="${USERNAME:-sleep-api-test-user}"
REPOSITORY_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
COMPOSE_FILE="${REPOSITORY_ROOT}/docker-compose.yml"

if [[ ! "${USER_ID}" =~ ^[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}$ ]]; then
  echo "USER_ID must be a UUID." >&2
  exit 1
fi

if ! docker compose -f "${COMPOSE_FILE}" ps --status running --services | grep -qx db; then
  echo "The database container is not running. Start the project first with 'docker-compose up --build' from the repository root." >&2
  exit 1
fi

docker compose -f "${COMPOSE_FILE}" exec -T db \
  psql -U user -d postgres -v ON_ERROR_STOP=1 \
  -v user_id="${USER_ID}" -v username="${USERNAME}" <<'SQL'
insert into t_user (id, username)
values (:'user_id'::uuid, :'username')
on conflict (id) do nothing;
SQL

printf 'Test user is ready. Run: USER_ID=%s ./scripts/test-api.sh\n' "${USER_ID}"
