cd ..
kubectl delete -f base/configmap.yaml
kubectl delete -f base/postgres.yaml
kubectl delete -f base/deployment.yaml
kubectl delete -f base/service.yaml
kubectl delete -f base/secret.yaml
kubectl delete -f base/box-recommend-app.yaml