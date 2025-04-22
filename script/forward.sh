# application server 로컬 연결
kubectl port-forward svc/box-recommend 30080:80
# db 로컬 연결
kubectl port-forward svc/postgres 5432:5432
# argo cd ui 로컬 연결
kubectl port-forward svc/argocd-server 8080:80