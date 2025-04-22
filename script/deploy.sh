cd ..
kubectl create namespace box-recommend
kubectl apply -f kubernetes/configmap.yaml
kubectl apply -f kubernetes/postgres.yaml
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/secret.yaml # 순서 중요(1)
kubectl apply -f kubernetes/service.yaml # 순서 중요(2)

