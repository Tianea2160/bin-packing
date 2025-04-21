# Box Recommend – 3D Bin Packing Optimizer with OptaPlanner & Spring Batch

이 프로젝트는 다양한 크기와 모양의 물건(Item)을 주어진 박스(Bin)에 3차원으로 최적으로 적재하기 위한 시스템입니다.  
OptaPlanner 기반의 휴리스틱 탐색을 통해 각 아이템의 위치, 방향, 회전 상태를 고려한 최적 배치를 수행하며,  
Spring Batch를 활용해 자동화된 배치 작업으로 적재 로직을 정기 실행합니다.

## 기능 요약

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
- **Spring Batch 통합**:
  - 배치 작업 자동 실행 (`@Scheduled`)
  - `ItemReader` → `ItemProcessor`(OptaPlanner) → `ItemWriter(Logger 출력)`
- **콘솔 기반 XY 평면 시각화 출력**:
  - Z-slice 기반의 XY 평면 출력 (`printXYProjection`)
  - logger 기반 structured output
- **JavaFX 기반 3D 시각화**:
  - Bin, Item을 실시간 3D로 시각화
  - Item별 고유 색상 + 투명도 + 테두리 표현
  - XYZ 라벨 및 좌표축 표시
  - 카메라 조정 가능 (대각/정면/XY 평면)

## 기능 순서도
<img  src="https://github.com/user-attachments/assets/2e5c8589-b021-4ea8-9308-7a0f863bd8e9" width=500>

## 사용 기술

- Kotlin
- Spring Batch (3.4.4)
- OptaPlanner
- JavaFX (3D 시각화)
- PostgreSQL (Docker 기반 개발 환경)
- SLF4J 기반 로깅 출력

## 실행 방법

1. PostgreSQL 실행

```bash
docker compose up -d
```

2. 프로젝트 실행

```bash
./gradlew bootRun
```

정기적으로 배치 작업이 실행되며, 결과는 로그로 출력됩니다.

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

## JavaFX 시각화 예시

<img width="912" alt="3D Packing" src="https://github.com/user-attachments/assets/cdc613f3-9628-4e34-a73b-9d6765a4b070" />

## 테스트

```bash
./gradlew test
```

제약 조건에 대한 ConstraintVerifier 기반 단위 테스트 포함
