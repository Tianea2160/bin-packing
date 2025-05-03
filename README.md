# Box Recommend – 3D Bin Packing Optimizer with OptaPlanner & Spring Batch

이 프로젝트는 다양한 크기와 모양의 물건(Item)을 주어진 박스(Bin)에 3차원으로 최적으로 적재하기 위한 시스템입니다.  
OptaPlanner 기반의 휴리스틱 탐색을 통해 각 아이템의 위치, 방향, 회전 상태를 고려한 최적 배치를 수행하며,  
Spring Batch를 활용해 자동화된 배치 작업으로 적재 로직을 정기 실행합니다.

## 기능 요약

### 핵심 알고리즘 기능
- **3차원 적재**: 가로, 세로, 높이 및 회전 고려
- **도형 형태 반영**: BOX, CYLINDER, CONE, POUCH
- **회전 지원**: 6가지 방향(Rotation) 조합
- **버퍼 비율 고려**: Bin의 내부 여유 공간 고려
- **물리 제약 조건 지원**:
  - 무게 제한 (maxWeight)
  - 부피 제한
  - 겹침 방지
  - 박스 외부 초과 방지
- **접을 수 있는 상품 처리**: collapsible = true
- **점수 시스템**: BendableScore 기반 제약 목적별 점수 분석

### 시스템 기능
- **Spring Batch 통합**:
  - 배치 작업 자동 실행
  - `ItemReader` → `ItemProcessor`(OptaPlanner) → `ItemWriter`
- **RESTful API**: 배치 작업 실행 및 모니터링
- **데이터 저장**: PostgreSQL + Elasticsearch
- **시각화**:
  - 콘솔 기반 XY 평면 출력
  - JavaFX 기반 3D 시각화
  - Grafana 대시보드 모니터링
- **컨테이너화**: Docker & Kubernetes 지원
- **GitOps 배포**: ArgoCD를 통한 자동화된 배포

## 시스템 아키텍처

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Spring Boot   │────▶│    OptaPlanner  │────▶│     결과 저장     │
│  Application    │     │   Bin Packing   │     │ PostgreSQL      │
│                 │     │     Engine      │     │ Elasticsearch   │
└─────────────────┘     └─────────────────┘     └─────────────────┘
         ▲                                              │
         │                                              │
         │                                              ▼
┌─────────────────┐                          ┌─────────────────┐
│  REST API       │                          │   Monitoring    │
│  Swagger UI     │                          │ Grafana/Prometheus│
└─────────────────┘                          └─────────────────┘
```

## 기능 순서도
<img  src="https://github.com/user-attachments/assets/2e5c8589-b021-4ea8-9308-7a0f863bd8e9" width=500>

## 사용 기술

### 백엔드
- Kotlin 1.9.25
- Spring Boot 3.4.4
- Spring Batch 3.4.4
- OptaPlanner 10.0.0
- PostgreSQL 16
- Elasticsearch 8.11.3

### 인프라
- Docker & Docker Compose
- Kubernetes
- ArgoCD
- Prometheus & Grafana

### 시각화
- JavaFX 21.0.2 (3D 시각화)
- Grafana (모니터링 대시보드)

## 실행 방법

### 로컬 실행
1. Docker로 전체 스택 실행

```bash
docker-compose up -d
```

2. 애플리케이션 실행

```bash
./gradlew bootRun
```

### API 사용
batch job 실행:
```bash
curl -X POST http://localhost:8888/box-recommend/run
```

### Kubernetes 배포

1. 클러스터에 배포
```bash
cd script
./deploy.sh
```

2. 포트 포워딩
```bash
./forward.sh
```

## 모니터링

### Grafana 대시보드
JVM 메트릭 모니터링을 통해 애플리케이션 상태를 실시간으로 확인할 수 있습니다.

<img width="1265" alt="Grafana Dashboard" src="https://github.com/user-attachments/assets/grafana-dashboard.png" />

### 주요 모니터링 지표
- JVM 메모리 사용량
- Heap 메모리 사용률
- 스레드 상태
- GC 일시 정지 시간
- CPU 사용량
- Direct/Mapped 버퍼

## 예시 로그 출력

```
=== Batch step completed ===
Score: 0hard/1soft
Bin ID: 1
Bin 1 [XY 평면 @ Z=0]
| 1 | 2 |   |
| 3 |   |   |
|   |   |   |

Bin 1 [XY 평면 @ Z=1]
| 4 | 5 |   |
|   |   |   |
|   |   |   |
```

## JavaFX 3D 시각화

<img width="912" alt="3D Packing" src="https://github.com/user-attachments/assets/cdc613f3-9628-4e34-a73b-9d6765a4b070" />

## API 문서

애플리케이션 실행 후 Swagger UI에서 API 문서를 확인할 수 있습니다:
- http://localhost:8888/swagger-ui.html

## 테스트

```bash
./gradlew test
```

제약 조건에 대한 ConstraintVerifier 기반 단위 테스트 포함

## 아키텍처 디테일

### 배치 작업 흐름
1. **ItemReader**: SKU 및 Box 데이터 읽기
2. **ItemProcessor**: OptaPlanner로 최적화 로직 수행
3. **ItemWriter**: 결과를 PostgreSQL과 Elasticsearch에 저장

### 이벤트 기반 아키텍처
- 배치 결과 저장 시 `RecommendResultSavedEvent` 발행
- `@TransactionalEventListener`를 통한 비동기 Elasticsearch 저장

### 제약 조건 시스템
- `BendableScore`: 하드/소프트 제약 구분
- 물리적 제약 (겹침, 무게 제한) → 하드 스코어
- 최적화 목표 (공간 활용, 안정성) → 소프트 스코어

## 라이센스

Apache License 2.0
