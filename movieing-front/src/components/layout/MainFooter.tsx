import "./MainFooter.css";

export default function MainFooter() {
  return (
    <footer className="main-footer">
      <div className="main-footer__inner">
        <span>© {new Date().getFullYear()} Movieing</span>
      </div>
    </footer>
  )
}