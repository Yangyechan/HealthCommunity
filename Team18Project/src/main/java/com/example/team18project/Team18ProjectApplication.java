package com.example.team18project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Team18ProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(Team18ProjectApplication.class, args);
    }

}

/*
GitHub 사용법 Memo

1. 팀원 각자는 자신이 원하는 local환경에  git clone을 받는다. git clone ~  딱 1번
2. 각자의 코드 개발
3. git add . (local git repository에 add)
4. git commit -m "메세지" (local 에 commit & 메세지)
5. git push origin main xxxxxxxxxxxxxxxxxxxxxxx 절대 금지 최종 product 이다. + 배제우의 branch
6. git checkout -b freshman  // freshman ==  예시 입니다. 각자의 이름으로 하세요 ㅎㅎ  한번 바꾸면 쭉 이 브랜치를 사용합니다 브랜치명이 main이 아닌지 확인해주세요
7. git push origin freshman  //원격 저장소 freshman 브랜치에 push
8. compare & pull request  // branch merge 하고 싶어요 라고 요청! 자신이 작성한 코드 설명을 해주세요!
9. 저는 그럼 pull requests 보고 merge 하겠습니다. 또는 대화를 하겠죠? 예시) 이 부분 이상하다
10. 그럼 main branch 에 추가한 코드 +

11. 개발을 하던 도중에 main branch에 변화가 생기면 동기화를 해줘야 합니다 (pull!)
11-1 지금까지 자신이 local에서 개발한 내용을 저장을 합니다. -> git add .
11-2 git commit -m "커밋 메세지"
11-3 git pull origin main (동기화)
11-4 자신의 코드가 완성되면 git push origin main (원격 저장소에 저장) 자신의 branch에 다시 8번 과정 반복입니다 ㅎㅎ

 */