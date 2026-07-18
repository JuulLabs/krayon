#!/usr/bin/env bash

npm install @mermaid-js/mermaid-cli
for file in *.mermaid; do
  ./node_modules/.bin/mmdc npx mmdc \
    --backgroundColor transparent \
    --input "$file" \
    --output "../src/commonMain/composeResources/drawable/${file%.mermaid}.png"
done
