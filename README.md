# 🎬 Movieing – 영화 예매 서비스

Movieing은 실제 영화관 예매 흐름을 분석하여 설계한 개인 프로젝트입니다.  
영화 → 상영 일정 → 좌석 → 예매 → 결제까지의 도메인을 직접 모델링하고,  
**상태 기반 설계와 트랜잭션 관리**에 중점을 두어 구현했습니다.

단순 CRUD 구현이 아닌,  
예매/결제 과정에서 발생할 수 있는 **데이터 정합성 문제와 예외 상황**을 고려하여  
실제 서비스에 가까운 구조를 목표로 개발했습니다.

---

## 📁 프로젝트 구조

```text
movieing/
 ├ backend/        # Spring Boot (Java 21)
 ├ frontend/       # React + TypeScript
 ├ docs/           # ERD, Architecture, Diagrams
 └ README.md
