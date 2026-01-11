import { useMemo } from "react";
import "./AdminDashboard.css";

type StatCard = {
  label: string;
  value: string;
  sub?: string;
};

type NoticeItem = {
  id: string;
  title: string;
  desc?: string;
  level?: "INFO" | "WARN" | "ERROR";
};

type QuickAction = {
  label: string;
  desc: string;
  onClick: () => void;
};

export default function AdminDashboard() {
  // ✅ 나중에 API 붙일 자리 (현재는 더미)
  const stats: StatCard[] = useMemo(
    () => [
      { label: "오늘 예매 수", value: "128", sub: "전일 대비 +12" },
      { label: "오늘 매출", value: "₩ 3,840,000", sub: "결제 완료 기준" },
      { label: "상영 중 영화", value: "24", sub: "활성 상태" },
      { label: "활성 상영관", value: "12", sub: "운영 중 지점" },
    ],
    []
  );

  const notices: NoticeItem[] = useMemo(
    () => [
      {
        id: "n1",
        title: "결제 실패 건이 증가했어요",
        desc: "최근 1시간 동안 7건 발생",
        level: "WARN",
      },
      {
        id: "n2",
        title: "금일 23:00 이후 스케줄이 비어있는 상영관이 있어요",
        desc: "스케줄 등록이 필요합니다",
        level: "INFO",
      },
      {
        id: "n3",
        title: "좌석 매핑 누락 상영관 발견",
        desc: "좌석 관리에서 확인해주세요",
        level: "ERROR",
      },
    ],
    []
  );

  const recentBookings = useMemo(
    () => [
      { id: "B-240111-001", movie: "영화 제목 1", theater: "Movieing 합정", time: "14:10", status: "CONFIRMED" },
      { id: "B-240111-002", movie: "영화 제목 2", theater: "Movieing 강남", time: "14:22", status: "PAY_FAILED" },
      { id: "B-240111-003", movie: "영화 제목 3", theater: "Movieing 잠실", time: "14:35", status: "CANCELED" },
      { id: "B-240111-004", movie: "영화 제목 4", theater: "Movieing 합정", time: "14:49", status: "CONFIRMED" },
      { id: "B-240111-005", movie: "영화 제목 5", theater: "Movieing 홍대", time: "15:02", status: "CONFIRMED" },
    ],
    []
  );

  const quickActions: QuickAction[] = useMemo(
    () => [
      { label: "영화 등록", desc: "신규 영화 추가", onClick: () => (window.location.href = "/admin/movies") },
      { label: "스케줄 등록", desc: "상영 시간표 추가", onClick: () => (window.location.href = "/admin/schedules") },
      { label: "좌석 관리", desc: "좌석 배치/상태 확인", onClick: () => (window.location.href = "/admin/seats") },
      { label: "예매/결제", desc: "상태 변경/환불 확인", onClick: () => (window.location.href = "/admin/bookings") },
    ],
    []
  );

  const levelLabel = (lv?: NoticeItem["level"]) => {
    if (lv === "ERROR") return "긴급";
    if (lv === "WARN") return "주의";
    return "안내";
  };

  const bookingStatusLabel = (s: string) => {
    if (s === "CONFIRMED") return "확정";
    if (s === "PAY_FAILED") return "결제실패";
    if (s === "CANCELED") return "취소";
    return s;
  };

  return (
    <div className="admin-dashboard">
      <div className="admin-dashboard__header">
        <div>
          <h1 className="admin-dashboard__title">대시보드</h1>
          <p className="admin-dashboard__desc">오늘 운영 현황을 한눈에 확인하세요.</p>
        </div>

        <div className="admin-dashboard__actions">
          <button className="admin-dashboard__btn">새로고침</button>
          <button className="admin-dashboard__btn admin-dashboard__btn--primary">리포트</button>
        </div>
      </div>

      {/* Stat Cards */}
      <section className="admin-dashboard__stats">
        {stats.map((s) => (
          <div className="admin-dashboard__card" key={s.label}>
            <div className="admin-dashboard__cardLabel">{s.label}</div>
            <div className="admin-dashboard__cardValue">{s.value}</div>
            {s.sub && <div className="admin-dashboard__cardSub">{s.sub}</div>}
          </div>
        ))}
      </section>

      <div className="admin-dashboard__grid">
        {/* Notices */}
        <section className="admin-dashboard__panel">
          <div className="admin-dashboard__panelHeader">
            <h2 className="admin-dashboard__panelTitle">운영 알림</h2>
            <span className="admin-dashboard__panelHint">최근 이벤트</span>
          </div>

          <div className="admin-dashboard__noticeList">
            {notices.map((n) => (
              <div className="admin-dashboard__notice" key={n.id}>
                <span className={`admin-dashboard__badge admin-dashboard__badge--${(n.level || "INFO").toLowerCase()}`}>
                  {levelLabel(n.level)}
                </span>
                <div className="admin-dashboard__noticeBody">
                  <div className="admin-dashboard__noticeTitle">{n.title}</div>
                  {n.desc && <div className="admin-dashboard__noticeDesc">{n.desc}</div>}
                </div>
              </div>
            ))}
          </div>
        </section>

        {/* Quick actions */}
        <section className="admin-dashboard__panel">
          <div className="admin-dashboard__panelHeader">
            <h2 className="admin-dashboard__panelTitle">빠른 작업</h2>
            <span className="admin-dashboard__panelHint">자주 쓰는 메뉴</span>
          </div>

          <div className="admin-dashboard__quickGrid">
            {quickActions.map((q) => (
              <button key={q.label} className="admin-dashboard__quick" onClick={q.onClick} type="button">
                <div className="admin-dashboard__quickLabel">{q.label}</div>
                <div className="admin-dashboard__quickDesc">{q.desc}</div>
              </button>
            ))}
          </div>
        </section>

        {/* Recent bookings */}
        <section className="admin-dashboard__panel admin-dashboard__panel--span2">
          <div className="admin-dashboard__panelHeader">
            <h2 className="admin-dashboard__panelTitle">최근 예매</h2>
            <span className="admin-dashboard__panelHint">실시간 흐름</span>
          </div>

          <div className="admin-dashboard__table">
            <div className="admin-dashboard__thead">
              <div>예매번호</div>
              <div>영화</div>
              <div>극장</div>
              <div>시간</div>
              <div>상태</div>
            </div>

            {recentBookings.map((b) => (
              <div className="admin-dashboard__tr" key={b.id}>
                <div className="admin-dashboard__td admin-dashboard__mono">{b.id}</div>
                <div className="admin-dashboard__td">{b.movie}</div>
                <div className="admin-dashboard__td">{b.theater}</div>
                <div className="admin-dashboard__td">{b.time}</div>
                <div className="admin-dashboard__td">
                  <span className={`admin-dashboard__pill admin-dashboard__pill--${b.status.toLowerCase()}`}>
                    {bookingStatusLabel(b.status)}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
}
