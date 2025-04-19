# Box Recommend – 3D Bin Packing Optimizer with OptaPlanner
이 프로젝트는 다양한 크기와 모양의 물건(Item)을 주어진 박스(Bin)에 3차원으로 최적으로 적재하기 위한 시스템입니다.  
OptaPlanner 기반의 휴리스틱 탐색을 통해 각 아이템의 위치, 방향, 회전 상태를 고려한 최적 배치를 수행합니다.

## 기능 요약
- 3차원 적재: 가로, 세로, 높이 및 회전 고려
- 도형 형태 반영: BOX, CYLINDER, CONE, POUCH
- 회전 지원: 6가지 방향(Rotation) 조합
- 버퍼 비율 고려: Bin의 내부 여유 공간 고려
- 물리 제약 조건 지원:
    - 무게 제한 (maxWeight)
    - 부피 제한
    - 겹침 방지
    - 박스 외부 초과 방지
- 접을 수 있는 상품 처리 (collapsible = true)
- 콘솔 기반 3D 평면 시각화 출력: XY 평면을 Z축으로 슬라이스하여 표시
## 사용 기술
- Kotlin
- OptaPlanner
- JVM
- 콘솔 기반 시각화 (Z-slice 기반 XY 평면)
## 실행 방법

1. 프로젝트 빌드
```bash
./gradlew build
```

2. 메인 실행

```bash
./gradlew run
```


## 예시 출력

```
=== 결과 ===
Item 1 -> Bin 1 | Rotation: XYZ | X: 0, Y: 0, Z: 0
Item 2 -> Bin 1 | Rotation: YXZ | X: 1, Y: 0, Z: 0
...

Score: 0hard/0soft

Bin 1 [XY 평면 @ Z=0]
| 1 | 2 |   |
| 3 |   |   |
|   |   |   |

Bin 1 [XY 평면 @ Z=1]
| 4 | 5 |   |
|   |   |   |
|   |   |   |
```

## 테스트

```shell
./gradlew test
```
OptaPlanner의 제약 조건에 대해 ConstraintVerifier 기반 유닛 테스트가 포함되어 있습니다.

