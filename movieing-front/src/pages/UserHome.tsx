import MainLayout from "../components/layout/MainLayout";
import HeroBanner from "../components/userHome/HeroBanner";
import "./UserHome.css";

export default function UserHome() {
  return (
    <MainLayout>
      <div className="user-home">
        <HeroBanner />

        <div className="user-home__grid">
          <div className="user-home__left">
            {/* <QuickBooking /> */}
            {/* <EventStrip /> */}
          </div>

          <div className="user-home__right">
            {/* <NowShowing /> */}
          </div>
        </div>
      </div>
    </MainLayout>
  )
}