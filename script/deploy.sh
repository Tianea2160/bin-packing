cd ..
kubectl apply -k base/

# node port 를 로컬에서 사용할 수 있도록 세팅
kubectl port-forward svc/box-recommend 30080:80 &
kubectl port-forward svc/postgres 5432:5432 &