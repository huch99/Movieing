import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import './Home.css';
import { ACCESS_TOKEN_KEY } from "../../shared/api/api";

type TokenPayload = {
  role?: string;
}

function getTokenPayload(): TokenPayload | null {
  try {
    const token = localStorage.getItem(ACCESS_TOKEN_KEY);
    if (!token) return null;

    const payloadBase64 = token.split(".")[1];
    const decoded = atob(payloadBase64);
    return JSON.parse(decoded);
  } catch {
    return null;
  }
}

export default function Home() {
  const navigate = useNavigate();

  useEffect(() => {
    // ✅ ADMIN만 /admin으로 이동
    const payload = getTokenPayload();
    if (payload?.role === "ADMIN") {
      navigate("/admin", { replace: true });
    }
  }, [navigate]);

  return (
    <div>
      <div className="home">
        {/* Hero Section */}
        <section className="home-hero">
          <div className="home-hero__content">
            <h1 className="home-hero__title">
              지금 예매하고,
              <br />
              바로 영화관으로 🎬
            </h1>
            <p className="home-hero__desc">
              상영시간표 확인부터 좌석 선택까지
              <br />
              Movieing에서 한 번에
            </p>

            <div className="home-hero__actions">
              <button className="home-btn home-btn--primary">
                빠른 예매
              </button>
              <button className="home-btn">상영시간표</button>
            </div>
          </div>
        </section>

        {/* Movie List */}
        <section className="home-movies">
          <div className="home-section__header">
            <h2 className="home-section__title">현재 상영작</h2>
          </div>

          <div className="home-movie-grid">
            {Array.from({ length: 8 }).map((_, idx) => (
              <article className="home-movie-card" key={idx}>
                <div className="home-movie-card__poster" />
                <div className="home-movie-card__info">
                  <h3 className="home-movie-card__title">
                    영화 제목 {idx + 1}
                  </h3>
                  <p className="home-movie-card__meta">
                    예매율 00% · 평점 0.0
                  </p>
                  <button className="home-btn home-btn--small home-btn--primary">
                    예매하기
                  </button>
                </div>
              </article>
            ))}
          </div>
        </section>
      </div>
    </div>
  )
}