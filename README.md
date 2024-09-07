# VideoStatisticsService
이 서비스는 통계 관리 서비스로, VideoService에서 제공하는 영상 콘텐츠에 대해 일간, 주간, 월간 통계 데이터를 집계하여 다양한 지표와 수익 관련 데이터를 조회할 수 있는 API를 제공합니다.

영상 조회수, 광고 조회수, 재생 시간 등 핵심 성과 지표를 사용자와 게시물에 따라 효율적으로 집계하며 수익 분석을 위한 필수 데이터를 제공합니다.
이를 통해 사용자는 영상의 성과를 직관적으로 파악하고 전략적 의사 결정을 지원받을 수 있습니다.

대규모 데이터를 처리하기 위해서 배치작업을 도입하였으며, 성능 고도화를 통해 점진적으로 성능을 최적화하여 더 빠르고 안정적으로 통계 자료 집계를 구현하였습니다.
<br/><br/>

## 🪄 기능 요약

- **통계 자료 집계** : VideoService에서 제공하는 영상 게시물에 대한 통계자료를 일, 주, 월간으로 집계하고 저장합니다.
  - 영상 게시물에 대해 일, 주, 월간 기준으로 조회수, 광고 영상 조회수, 재생 시간 등을 집계합니다.
  - 영상 게시물에 대해 일일 수익금을 정산합니다.
  - 사용자의 모든 영상 게시물에 대해 일일 수익금을 합산하여 총 수익금을 정산합니다.
  - 모든 데이터는 매일 자정을 기준으로 주기적으로 집계됩니다.
    <br/><br/>

- **통계 자료 조회** : 집계된 각종 통계자료를 조회할 수 있습니다.
  - 사용자는 자신의 영상 게시물에 대한 일일 수익금을 조회할 수 있습니다.
  - 일, 주, 월간 기준으로 재생시간, 조회수 top5 영상을 조회할 수 있습니다. 
<br/><br/>

## 📖 배치 성능 개선
배치 작업 성능을 최적화 하여 더 빠르게 작업할 수 있는 환경을 조성하였습니다.

배치 작업 횟수가 증가함에 따라 배치 작업 시간이 계속해서 늘어나는 현상도 해결였습니다.
<br/><br/>

![image](https://github.com/user-attachments/assets/a55ed1ff-3257-4552-b3cc-eee16d5cb022)

| 개선 방식 | 배치작업 1회 | 배치작업 10회 | 시간 감소 비율 |
| ------------ | ------------- | ------------- | ------------- |
| proto | 240s  | 900s | 0% |
| reader/writer | 190s  | 800s | 15% |
| multi processor | 32s  | 300s | 75% |
| index | 17s  | 17s | 95% |

<br/>

- reader/writer 개선
  - page 방식으로 데이터를 읽어올 때 limit가 늘어남에 따라 데이터를 가져오는데 부담이 많이 되어 cursor 로 변경하였습니다.
  - 데이터를 읽어올 때 프로젝션으로 jpa 영속성관리 안되도록 설정하였습니다.
  - jpa를 사용해서 각 데이터마다 insert 쿼리가 나갔던 방식에서 jdbc를 사용한 batch insert로 한번에 쓰는 방식으로 변경하였습니다.
<br/><br/>

- multiproceesor 개선
  - 서버 환경에 맞춰 단일 쓰레드 방식에서 멀티 쓰레드 방식으로 배치작업을 실행할 수 있도록 변경하였습니다.
<br/><br/>

- index 개선
  - 거듭된 배치작업으로 DB에 데이터가 늘어날 수록 조건에 따라 데이터를 찾는데 큰 부담이 되었기 때문에 조건에 쓰이는 칼럼에 index를 추가하였습니다.

<br/><br/>


## 🛠 기술 스택

- <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> : 애플리케이션의 기반 프레임워크로, RESTful API 개발에 사용됩니다.
- <img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> : 인증 및 권한 관리를 위한 보안 프레임워크입니다.
- <img src="https://img.shields.io/badge/spring eureka-6DB33F?style=for-the-badge&logo=icloud&logoColor=white"> : 서비스 디스커버리를 위한 유레카 클라이언트로, 마이크로서비스 환경에서 서비스 등록 및 검색을 지원합니다.
- <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> : 관계형 데이터베이스로, 사용자 정보 및 영상 게시물 데이터를 관리합니다.
- <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white"> : 사용자 인증 및 세션 관리를 위한 토큰 기반 인증 방식입니다.
- <img src="https://img.shields.io/badge/spring batch-6DB33F?style=for-the-badge&logo=ebox&logoColor=white"> : 대규모 데이터를 안전하고 효율적으로 처리하기 위해 사용되었습니다.
- <img src="https://img.shields.io/badge/REST Docs-6DB33F?style=for-the-badge&logo=readthedocs&logoColor=white"> : API 문서를 자동으로 생성하는 도구로, 테스트코드를 기반으로 RESTful API 명세를 문서화합니다.
<br/><br/>

## 📖 API 명세

- API 명세는 [Spring REST Docs](https://spring.io/projects/spring-restdocs)를 사용하여 자동으로 생성되었습니다.
- 상세한 API 명세와 사용법은 `api.html` 파일을 통해 확인할 수 있습니다. 이 문서는 각 API의 엔드포인트, 요청 및 응답 형식, 예제 등을 포함합니다.
<br/><br/>


## ⚙️ 프로젝트 구조
- 프로젝트는 각 도메인별로 계층 구조를 가지도록 설계되었습니다.
- 데이터는 Mysql에 저장됩니다.
- 서비스는 Spring cloud Eureka에 등록되어 관리됩니다.
- Eureka에 등록된 VideoService와 통신하여 매일 영상 게시물에 대한 자료를 가져오고 집계합니다.
  <br/><br/>

![image](https://github.com/user-attachments/assets/a9304eed-d98a-4143-95a1-71badcab8565)



<br/><br/>
