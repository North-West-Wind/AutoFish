#!/bin/bash
# Stolen from Faboslav
# https://github.com/Faboslav/friends-and-foes/blob/master/.github/scripts/generate-publish-matrix.sh

matrix_content="{\"include\":["
enabled_platforms=$(awk -F= '/stonecutter_enabled_platforms/{print $2}' gradle.properties | tr -d ' ')

for platform in $(echo $enabled_platforms | tr ',' ' '); do
  versions=$(awk -F= '/stonecutter_enabled_'$platform'_versions/{print $2}' gradle.properties | tr -d ' ')
  for version in $(echo $versions | tr ',' ' '); do
    if [[ "$platform" == "fabric" ]]; then
      supported_loaders="\"fabric\",\"quilt\""
    else
      supported_loaders="\"$platform\""
    fi

    matrix_entry="{\"loader\":\"$platform\",\"version\":\"$version\",\"supported_loaders\":[$supported_loaders]},"
    matrix_content+="$matrix_entry"
  done
done

matrix_content="${matrix_content%,}]}"
echo "Generated matrix: $matrix_content"
echo "matrix=$matrix_content" >> $GITHUB_OUTPUT