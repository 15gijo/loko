ARG ELASTIC_VERSION

# https://www.docker.elastic.co/
# hadolint ignore=DL1000
FROM docker.elastic.co/elasticsearch/elasticsearch:${ELASTIC_VERSION:-8.17.4}

ENTRYPOINT ["/entrypoint.sh"]
