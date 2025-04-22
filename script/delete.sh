cd ..
kubectl delete -f kubernetes/configmap.yaml
kubectl delete -f kubernetes/postgres.yaml
kubectl delete -f kubernetes/deployment.yaml
kubectl delete -f kubernetes/service.yaml
kubectl delete -f kubernetes/secret.yaml