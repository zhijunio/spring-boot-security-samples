#!/usr/bin/env bash

set -euo pipefail

scope="${1:-.}"
found=0

while IFS= read -r pom; do
    found=1
    echo "Testing ${pom}"
    mvn --batch-mode --no-transfer-progress -f "${pom}" test
done < <(find "${scope}" -type f -name pom.xml -not -path '*/target/*' | sort)

if [[ "${found}" -eq 0 ]]; then
    echo "No Maven projects found under ${scope}" >&2
    exit 1
fi
