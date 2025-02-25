<div align="center">

</div> 

# 📝 프로젝트 소개
### Order Delivery App
스프링 부트 기반 배달 및 포장 음식 주문 관리 플랫폼을 개발했습니다. <br />
광화문 근처에서 운영될 음식점들의 배달 및 포장 주문 관리, 결제, 그리고 주문 내역 관리 기능을 제공하는 플랫폼입니다.

<br />

### 📄 프로젝트 설계
- 초기 단계이기에 모놀리식 아키텍처로 개발하지만, 마이크로 서비스 아키텍처로 변경 용이한 구조를 생각하여 개발
- 도메인 주도 설계를 고려하여 ERD 설계
- 기능 개발 시 확장 가능성을 고려
<br />

### 🚩 프로젝트 목적
- AI API 연동 : AI API를 연동하여 가게 사장님이 상품 설명을 쉽게 작성할 수 있도록 지원
- 권한에 따른 접근 관리 : 고객, 가게 주인, 관리자 별로 데이터 및 페이지 접근 권한 제한
- 데이터 보존 및 삭제 처리 : soft delete 적용, 데이터 감사 로그 저장

<br />

# ⚙️ 서비스 구성 <br />
### 🙍‍♀️ 유저 <br />
- **고객:** 자신의 주문 내역만 조회 가능
- **가게 주인:** 자신의 가게 주문 내역, 가게 정보, 주문 처리 및 메뉴 수정 가능
- **관리자:** 모든 가게 및 주문에 대한 전체 권한 보유
- **회원 검색:** 회원 ID로 회원 검색 지원
  
### 🏪 가게 <br />
- **카테고리:** 음식점 카테고리로 가게 분류
- **지역:** 음식점의 위치로 가게 분류
- **가게 검색:** 가게 이름으로 가게 검색 지원

### 🍕 메뉴 및 옵션 <br />
- **메뉴 및 옵션 노출 상태:** 판매중, 숨김 처리, 매진 모두 지원
- **메뉴 검색:** 메뉴 이름으로 메뉴 검색 지원

### 🎞️ 이미지 <br />
- **메뉴 및 옵션 이미지 저장:** AWS S3를 연동하여 가게 사장님이 등록하는 메뉴 및 옵션 이미지를 저장
- **이미지 순서:** 이미지 등록시 순서를 고려하여 저장 및 노출
- **이미지 미리보기:** 이미지 등록시 미리보기를 제공하여 가게 사장님이 확인할 수 있도록 지원

### 🤖 가게 및 메뉴 설명 추천 AI <br />
- **가게 및 메뉴 설명 자동 생성:** AI API를 연동하여 가게 사장님이 가게 및 메뉴 설명을 쉽게 작성할 수 있도록 지원
- **AI 요청 기록:** AI API 요청 질문과 대답은 모두 데이터베이스에 저장

### 📜 주문 <br />
- **주문 취소:** 주문 생성 후 5분 이내에만 취소 가능하도록 제한
- **주문 유형:** 온라인 주문과 대면 주문(가게에서 직접 주문) 모두 지원
- **주문 검색:** 주문 ID로 주문 검색 지원

### 💳 결제 <br />
- **PG사 연동:** PG사와의 결제 연동은 추후 진행, 결제 관련 내역만 플랫폼의 데이터베이스에 저장
- **결제 검색:** 결제 ID로 결제 검색 지원

### 📦 배달 <br />
- **배달 유형:** 배송지로 배달과 고객 직접 픽업 모두 지원
- **배달 검색:** 회원 ID로 배달 검색 지원

### ⭐ 리뷰 <br />
- **리뷰 등록:** 주문을 통해 가게의 리뷰 및 평점을 저장

<br />

## 💁‍♂️ 프로젝트 팀원
|팀리더|테크리더|팀원|팀원|
|:---:|:---:|:---:|:---:|
|![Image](https://github.com/user-attachments/assets/991ca779-80a6-455e-8db3-25e77e1c965d) | ![Image](https://github.com/user-attachments/assets/88e733b5-8f09-4c5a-b14d-6243bbcf756d) |![Image](https://github.com/user-attachments/assets/d85abdfc-3e8e-4db4-828b-7eeb9cd5bb8c) | ![Image](https://github.com/user-attachments/assets/773cb7d4-787f-49f9-a494-835fd356ef31) |
|[전진우]|[권수연]|[김도원]|[양수영]|
|주문/배달/결제 담당|메뉴/옵션/이미지 담당|유저 담당|가게/카테고리/지역/리뷰 담당|

## ERD
👉🏻 [ERD] https://dbdiagram.io/d/delivery-platform-MSA-67ada54c263d6cf9a00276cf
![Image](https://github.com/user-attachments/assets/8d24131c-3420-40a8-83de-3f0cae4a4897)

<br />

## ⚙ 기술 스택

### Back-end
<div>
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Java.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/SpringBoot.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/SpringSecurity.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/SpringDataJPA.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/JWT.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Qeurydsl.png?raw=true" width="80">
<img src="https://github.com/user-attachments/assets/08e68472-fbdc-4a86-a2af-b28bb37ff132?raw=true" width="80" height="85">
</div>

### Infra
<div>
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/AWSEC2.png?raw=true" width="80">
<img src="https://github.com/user-attachments/assets/d5fdd0bb-2308-47a6-99e7-17d628ac905e?raw=true" width="80" height="80">



### Tools
<div>
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Github.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Notion.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Figma.png?raw=true" width="80">
</div>

<br />

## 🛠️ 프로젝트 아키텍쳐
![Image](https://github.com/user-attachments/assets/4cebee88-0c48-487b-9358-1db56a36d649)
