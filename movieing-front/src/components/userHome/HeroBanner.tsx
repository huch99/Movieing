import "./HeroBanner.css";

export default function HeroBanner() {
  return (
    <section className="hero">
      <div className="hero__inner">
        <div className="hero__content">
          <div className="hero__badge">NOW SHOWING</div>
          <h2 className="hero__title">이번 주 화제의 영화</h2>
          <p className="hero__desc">
            예매 오픈 · 특별관 · 이벤트까지 한 번에 확인하세요.
          </p>

          <div className="hero__actions">
            <button className="hero__btn hero__btn--primary">예매하기</button>
            <button className="hero__btn">상영작 보기</button>
          </div>
        </div>

        <div className="hero__poster" aria-hidden="true">
          <div className="hero__poster-box" />
          <div className="hero__poster-shadow" />
        </div>
      </div>
    </section>
  )
}