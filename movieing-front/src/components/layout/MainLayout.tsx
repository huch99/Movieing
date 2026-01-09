import './MainLayout.css';
import MainHeader from "./MainHeader";
import MainSection from "./MainSection";
import MainFooter from "./MainFooter";
import type { ReactNode } from 'react';

type Props = {
  children: ReactNode;
};

export default function MainLayout({ children }: Props) {
    return (
        <div className="main-layout">
            <MainHeader />
            <MainSection>{children}</MainSection>
            <MainFooter />
        </div>
    );
}
