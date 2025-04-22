cd ..
kubectl create namespace box-recommend
kubectl apply -f base/configmap.yaml
kubectl apply -f base/postgres.yaml
kubectl apply -f base/deployment.yaml
kubectl apply -f base/secret.yaml # 순서 중요(1)
kubectl apply -f base/service.yaml # 순서 중요(2)
kubectl apply -f base/box-recommend-app.yaml
