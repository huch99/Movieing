import "./AdminPage.css";

type AdminCardProps = {
  title: string;
  desc: string;
  buttonText: string;
  onClick?: () => void;
};

function AdminCard({ title, desc, buttonText, onClick }: AdminCardProps) {
  return (
    <article className="admin-card">
      <div className="admin-card__title">{title}</div>
      <div className="admin-card__desc">{desc}</div>
      <button className="admin-btn admin-btn--primary" type="button" onClick={onClick}>
        {buttonText}
      </button>
    </article>
  );
}

export default function AdminPage() {
  return (
    <div className="admin-page">
      <div className="admin-main__head">
        <h1 className="admin-title">대시보드</h1>
        <p className="admin-subtitle">
          운영 현황을 한 눈에 확인하고 빠르게 관리 작업을 시작하세요.
        </p>
      </div>

      <section className="admin-stats" aria-label="admin stats">
        <div className="stat">
          <div className="stat__label">오늘 예매</div>
          <div className="stat__value">0</div>
        </div>
        <div className="stat">
          <div className="stat__label">결제 완료</div>
          <div className="stat__value">0</div>
        </div>
        <div className="stat">
          <div className="stat__label">상영중 영화</div>
          <div className="stat__value">0</div>
        </div>
        <div className="stat">
          <div className="stat__label">등록 극장</div>
          <div className="stat__value">0</div>
        </div>
      </section>

      <section className="admin-grid" aria-label="admin quick actions">
        <AdminCard
          title="영화 등록"
          desc="새로운 영화를 등록하고 포스터/상세 정보를 관리합니다."
          buttonText="영화 등록"
        />
        <AdminCard
          title="극장 등록"
          desc="지점(극장) 정보를 등록하고 운영 상태를 관리합니다."
          buttonText="극장 등록"
        />
        <AdminCard
          title="상영관/좌석"
          desc="상영관, 좌석 배치 및 활성 상태를 관리합니다."
          buttonText="상영관 관리"
        />
        <AdminCard
          title="상영 스케줄"
          desc="상영시간표를 생성/수정하고 운영 상태를 관리합니다."
          buttonText="스케줄 관리"
        />
      </section>

      <section className="admin-panel" aria-label="admin panel">
        <div className="admin-panel__title">최근 작업</div>
        <div className="admin-panel__empty">아직 표시할 데이터가 없어요.</div>
      </section>
    </div>
  );
}
