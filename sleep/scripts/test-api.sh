#!/usr/bin/env bash
set -euo pipefail

API_URL="${API_URL:-http://localhost:8080}"
: "${USER_ID:?Set USER_ID to the UUID of an existing user in t_user}"

if ! curl --fail --silent "${API_URL}/health" >/dev/null; then
  echo "The API is not reachable at ${API_URL}. Start the project with 'docker-compose up --build' from the repository root, or set API_URL to a running instance." >&2
  exit 1
fi

today="$(date +%F)"
yesterday="$(date -d 'yesterday' +%F)"
headers=(-H "X-User-Id: ${USER_ID}")

request() {
  curl --silent --show-error --output /dev/stderr --write-out '\n%{http_code}' "$@"
}

echo "Creating sleep log for ${today}..."
create_result="$(request -X POST "${API_URL}/sleeplogs" "${headers[@]}" \
  -H 'Content-Type: application/json' \
  --data "{\"from\":\"${yesterday}T22:30:00\",\"to\":\"${today}T06:30:00\",\"feeling\":\"GOOD\"}")"
create_status="$(printf '%s\n' "${create_result}" | tail -n 1)"
if [[ "${create_status}" != "201" && "${create_status}" != "409" ]]; then
  echo "Expected create to return 201 or 409 for an existing daily log, got ${create_status}." >&2
  exit 1
fi

echo "Fetching last night's sleep log..."
last_night_status="$(request -X GET "${API_URL}/sleeplogs" "${headers[@]}" | tail -n 1)"
if [[ "${last_night_status}" != "200" ]]; then
  echo "Expected last-night lookup to return 200, got ${last_night_status}." >&2
  exit 1
fi

echo "Fetching 30-day averages..."
averages_status="$(request -X GET "${API_URL}/sleeplogs/averages" "${headers[@]}" | tail -n 1)"
if [[ "${averages_status}" != "200" ]]; then
  echo "Expected averages lookup to return 200, got ${averages_status}." >&2
  exit 1
fi

echo "All API smoke tests passed."
