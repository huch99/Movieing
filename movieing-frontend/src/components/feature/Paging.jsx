import React, { useState } from 'react';

const Paging = (loading, item) => {
    const [page, setPage] = useState(0);
    
    // 페이징 기능 - 이전 페이지
    const goPrev = () => {
        const next = Math.max(0, page - 1);
        setPage(next);
        load(next);
    }

    // 페이징 기능 - 다음 페이지
    const goNext = () => {
        const next = Math.min(totalPages - 1, page + 1);
        setPage(next);
        load(next);
    };

    return (
        <div className="pager">
            <button onClick={goPrev} disabled={loading || page <= 0}>
                이전
            </button>
            <div className="pager__info">
                {totalPage ? `${page + 1} / ${totalPage}` : "-"}
            </div>
            <button
                onClick={goNext}
                disabled={loading || page + 1 >= totalPage}
            >
                다음
            </button>
        </div>
    );
};

export default Paging;