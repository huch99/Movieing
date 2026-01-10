import './Section.css';

export default function Section() {
  return (
    <div className="section">
      {/* Hero */}
      <section className="section-hero">
        <div className="section-hero__content">
          <h1 className="section-hero__title">
            지금 예매하고,
            <br />
            바로 영화관으로
          </h1>
          <p className="section-hero__desc">
            상영시간표 확인부터 좌석 선택까지
            <br />
            Movieing에서 한 번에.
          </p>

          <div className="section-hero__actions">
            <button className="ui-btn ui-btn--primary" type="button">
              빠른 예매
            </button>
            <button className="ui-btn" type="button">
              상영시간표
            </button>
          </div>
        </div>

        <div className="section-hero__poster" aria-hidden="true">
          <div className="poster-skeleton" />
        </div>
      </section>

      {/* Movies */}
      <section className="section-block">
        <div className="section-head">
          <h2 className="section-title">현재 상영작</h2>
          <div className="tabs" role="tablist" aria-label="movie tabs">
            <button className="tab active" type="button" role="tab" aria-selected="true">
              박스오피스
            </button>
            <button className="tab" type="button" role="tab" aria-selected="false">
              상영중
            </button>
            <button className="tab" type="button" role="tab" aria-selected="false">
              개봉예정
            </button>
          </div>
        </div>

        <div className="movie-grid">
          {Array.from({ length: 8 }).map((_, i) => (
            <article className="movie-card" key={i}>
              <div className="movie-poster" />
              <div className="movie-info">
                <div className="movie-title">영화 제목 {i + 1}</div>
                <div className="movie-meta">
                  <span className="badge">예매율 00%</span>
                  <span className="dot">•</span>
                  <span>평점 0.0</span>
                </div>
                <div className="movie-actions">
                  <button className="ui-btn ui-btn--small" type="button">
                    상세
                  </button>
                  <button className="ui-btn ui-btn--small ui-btn--primary" type="button">
                    예매
                  </button>
                </div>
              </div>
            </article>
          ))}
        </div>
      </section>

      {/* Quick Booking */}
      <section className="section-block">
        <h2 className="section-title">빠른 예매</h2>

        <div className="quick-box">
          <div className="quick-col">
            <label className="field-label">영화</label>
            <select className="field">
              <option>영화 선택</option>
            </select>
          </div>

          <div className="quick-col">
            <label className="field-label">극장</label>
            <select className="field">
              <option>극장 선택</option>
            </select>
          </div>

          <div className="quick-col">
            <label className="field-label">날짜</label>
            <input className="field" type="date" />
          </div>

          <div className="quick-col">
            <label className="field-label">시간</label>
            <select className="field">
              <option>시간 선택</option>
            </select>
          </div>

          <div className="quick-col quick-col--action">
            <button className="ui-btn ui-btn--primary ui-btn--wide" type="button">
              좌석 선택
            </button>
          </div>
        </div>
      </section>
    </div>
  )
}