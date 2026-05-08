# Gemini 개발 지침: Java Swing 일기장 프로그램

## 목표

Java Swing으로 일기장 프로그램을 만든다.  
MySQL DB와 연동하고, 코드는 최대한 간단하게 유지한다.

핵심 원칙은 다음과 같다.

- UI, DTO, DB 연결, DAO 파일을 분리한다.
- 각 클래스는 한 가지 역할만 맡게 해서 응집도를 높인다.
- 초보자가 읽기 쉬운 코드로 작성한다.
- 화면은 파스텔 계열 색상과 이미지를 사용해서 예쁘게 만든다.

## 추천 프로젝트 구조

```text
src/
└── diary/
    ├── Main.java
    ├── dto/
    │   └── DiaryDto.java
    ├── db/
    │   └── DBUtil.java
    ├── dao/
    │   └── DiaryDao.java
    └── ui/
        └── DiaryFrame.java

resources/
└── diary.png
```

## 역할 분리 기준

### Main.java

프로그램 시작만 담당한다.

```java
public class Main {
    public static void main(String[] args) {
        new DiaryFrame();
    }
}
```

### DiaryDto.java

데이터 전달용 bag 역할만 한다.  
DB 조회 결과나 UI 입력값을 담는 용도로만 사용한다.

필드 예시:

- id
- title
- content
- weather
- createdDate

DTO에는 DB 코드나 Swing 코드를 넣지 않는다.

### DBUtil.java

MySQL 연결만 담당한다.

담당 코드:

- JDBC Driver 로딩
- DB URL, 계정, 비밀번호 관리
- Connection 생성

DB 연결 정보 예시:

```java
private static final String URL = "jdbc:mysql://localhost:3306/diary_db?serverTimezone=Asia/Seoul";
private static final String USER = "root";
private static final String PASSWORD = "1234";
```

### DiaryDao.java

DB 작업만 담당한다.

필수 기능:

- 일기 저장
- 일기 목록 조회
- 일기 상세 조회
- 일기 수정
- 일기 삭제

DAO에는 Swing UI 코드를 넣지 않는다.

### DiaryFrame.java

화면과 버튼 이벤트만 담당한다.

담당 기능:

- 제목 입력
- 날씨 선택
- 일기 내용 입력
- 저장, 수정, 삭제, 새로쓰기 버튼
- 일기 목록 표시
- 선택한 일기 내용 표시

UI 클래스 안에서 SQL을 직접 작성하지 않는다.  
DB 작업은 반드시 `DiaryDao`를 통해 호출한다.

## MySQL 테이블 예시

```sql
CREATE DATABASE diary_db;

USE diary_db;

CREATE TABLE diary (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    weather VARCHAR(30),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

## JDBC 드라이버

MySQL 연동을 위해 MySQL Connector/J가 필요하다.

IntelliJ에서 직접 jar를 추가하거나, Maven/Gradle을 사용한다면 다음 의존성을 추가한다.

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.4.0</version>
</dependency>
```

## UI 디자인 방향

일기장 느낌이 나도록 부드러운 색상을 사용한다.

추천 색상:

- 배경색: `#FFF7ED`
- 패널색: `#FFE4E6`
- 버튼색: `#F9A8D4`
- 강조색: `#FB7185`
- 글자색: `#374151`

추천 폰트:

```java
new Font("Malgun Gothic", Font.PLAIN, 14)
```

버튼은 둥근 느낌과 여백을 주고, 화면 전체가 답답하지 않게 만든다.

## 이미지 사용

`resources/diary.png` 이미지를 준비해서 화면 상단에 넣는다.

이미지 로딩 예시:

```java
ImageIcon icon = new ImageIcon("resources/diary.png");
Image image = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
JLabel imageLabel = new JLabel(new ImageIcon(image));
```

이미지가 없어도 프로그램이 죽지 않도록 처리한다.

## 코딩 스타일

- 클래스 이름은 PascalCase를 사용한다.
- 변수 이름은 camelCase를 사용한다.
- SQL은 `PreparedStatement`를 사용한다.
- 예외는 `try-catch`로 처리하고, 사용자에게는 `JOptionPane`으로 안내한다.
- 한 메서드가 너무 길어지면 작은 메서드로 분리한다.
- 복잡한 디자인 패턴은 사용하지 않는다.

## 구현 순서

1. MySQL에 `diary_db`와 `diary` 테이블을 만든다.
2. `DBUtil.java`에서 DB 연결을 만든다.
3. `DiaryDto.java`를 만든다.
4. `DiaryDao.java`에서 CRUD 기능을 만든다.
5. `DiaryFrame.java`에서 Swing 화면을 만든다.
6. 버튼 이벤트에서 DAO 메서드를 호출한다.
7. 이미지와 색상을 적용한다.
8. 저장, 조회, 수정, 삭제가 정상 동작하는지 테스트한다.

## 최종 프로그램 기능

- 일기 제목 작성
- 날씨 선택
- 일기 내용 작성
- 일기 저장
- 저장된 일기 목록 보기
- 목록에서 선택한 일기 불러오기
- 일기 수정
- 일기 삭제
- 파스텔 색상 UI
- 일기장 이미지 표시

## 주의사항

- UI 클래스에 SQL을 직접 쓰지 않는다.
- DTO 클래스에 로직을 많이 넣지 않는다.
- DB 연결 정보는 `DBUtil.java` 한 곳에서 관리한다.
- MySQL 비밀번호는 본인 환경에 맞게 수정한다.
- 이미지 경로가 틀리면 표시되지 않으므로 `resources/diary.png` 위치를 확인한다.
