import React, { type ReactNode } from 'react'
import "./MainSection.css";

type Props = {
    children: ReactNode;
}

export default function MainSection({children}: Props) {
  return (
    <main className="main-section">
      <div className="main-section__inner">{children}</div>
    </main>
  )
}