/** Shimmering placeholders shown while data is being fetched. */

export function SkeletonGrid({ count = 8 }: { count?: number }) {
  return (
    <div className="grid" aria-busy="true" aria-label="Loading">
      {Array.from({ length: count }, (_, index) => (
        <div key={index} className="stack" style={{ gap: 12 }}>
          <div className="skeleton" style={{ aspectRatio: "1", borderRadius: 12 }} />
          <div className="skeleton" style={{ height: 16, width: "80%" }} />
          <div className="skeleton" style={{ height: 12, width: "50%" }} />
        </div>
      ))}
    </div>
  );
}

export function SkeletonList({ count = 5 }: { count?: number }) {
  return (
    <div className="stack" aria-busy="true" aria-label="Loading">
      {Array.from({ length: count }, (_, index) => (
        <div key={index} className="row" style={{ gap: 16 }}>
          <div className="skeleton" style={{ width: 44, height: 44, borderRadius: 8 }} />
          <div className="stack" style={{ flex: 1, gap: 8 }}>
            <div className="skeleton" style={{ height: 14, width: "60%" }} />
            <div className="skeleton" style={{ height: 12, width: "35%" }} />
          </div>
        </div>
      ))}
    </div>
  );
}

export function SkeletonCards({ count = 6 }: { count?: number }) {
  return (
    <div className="grid grid-3" aria-busy="true" aria-label="Loading">
      {Array.from({ length: count }, (_, index) => (
        <div key={index} className="card stack" style={{ gap: 12 }}>
          <div className="skeleton" style={{ height: 20, width: "70%" }} />
          <div className="skeleton" style={{ height: 12 }} />
          <div className="skeleton" style={{ height: 12, width: "85%" }} />
        </div>
      ))}
    </div>
  );
}
