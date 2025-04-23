cd ..
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

kubectl apply -k base/
# argocd 비밀번호 조회하기
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d
# 새 비밀번호 해시 생성
NEW_PASSWORD="test1234"
# shellcheck disable=SC2016
BCRYPT_HASH=$(htpasswd -nbBC 10 "" $NEW_PASSWORD | tr -d ':\n' | sed 's/$2y/$2a/')

# argocd-secret에 새 비밀번호 패치
kubectl -n argocd patch secret argocd-secret \
  -p '{"stringData": {"admin.password": "'"$BCRYPT_HASH"'", "admin.passwordMtime": "'$(date +%FT%T%Z)'"}}'
