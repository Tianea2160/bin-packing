#!/bin/bash
# 이미지 빌드 및 Minikube에 로드하기
cd ..
# Minikube Docker 환경 사용
# shellcheck disable=SC2046
eval $(minikube docker-env)

# 이미지 빌드
docker build -t localhost:49410/box-recommend:latest .
docker push localhost:49410/box-recommend:latest

# Deployment 재시작하여 새 이미지 사용
kubectl rollout restart deployment box-recommend --namespace box-recommend

# Docker 환경 변수 초기화 (선택 사항)
# shellcheck disable=SC2046