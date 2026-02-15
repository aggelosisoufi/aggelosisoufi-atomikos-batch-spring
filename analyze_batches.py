import pandas as pd
import matplotlib.pyplot as plt

# ------------------------------
# Load CSVs
# ------------------------------

page_df = pd.read_csv("page_offset_results.csv")
slice_df = pd.read_csv("slice_offset_results.csv")
keyset_df = pd.read_csv("keyset_cursor_results.csv")

# ------------------------------
# Normalize structure
# Expecting columns: window, total_time_ms
# If your column naming differs, adjust below
# ------------------------------

def prepare(df, label):
    df = df.copy()

    # Create window index if missing
    if "window" not in df.columns:
        df["window"] = range(len(df))

    # Detect time column
    time_col = None
    for c in df.columns:
        if "time" in c:
            time_col = c
            break

    if time_col != "total_time_ms":
        df.rename(columns={time_col: "total_time_ms"}, inplace=True)

    df["method"] = label
    return df


page_df = prepare(page_df, "Offset Pagination")
slice_df = prepare(slice_df, "Slice Pagination")
keyset_df = prepare(keyset_df, "Keyset Cursor")

# ------------------------------
# Plot 1 — Retrieval Time per Window (500 rows per window)
# ------------------------------

plt.figure(figsize=(15, 8))
plt.plot(page_df["window"], page_df["total_time_ms"], label="Offset Pagination", marker='o')
plt.plot(slice_df["window"], slice_df["total_time_ms"], label="Slice Pagination", marker='o')
plt.plot(keyset_df["window"], keyset_df["total_time_ms"], label="Keyset Cursor", marker='o')

plt.title("Retrieval Time per 500-row Window", fontsize=16)
plt.xlabel("Window Index (each = 500 rows)", fontsize=14)
plt.ylabel("Retrieval Time (ms)", fontsize=14)
plt.grid(True, linestyle="--", alpha=0.5)
plt.legend()
plt.tight_layout()
plt.savefig("plot_retrieval_per_window.png", dpi=300)
plt.show()

# ------------------------------
# Plot 2 — Cumulative Time Comparison
# ------------------------------

page_df["cumulative_time"] = page_df["total_time_ms"].cumsum()
slice_df["cumulative_time"] = slice_df["total_time_ms"].cumsum()
keyset_df["cumulative_time"] = keyset_df["total_time_ms"].cumsum()

plt.figure(figsize=(15, 8))
plt.plot(page_df["window"], page_df["cumulative_time"], label="Offset Pagination", linewidth=2)
plt.plot(slice_df["window"], slice_df["cumulative_time"], label="Slice Pagination", linewidth=2)
plt.plot(keyset_df["window"], keyset_df["cumulative_time"], label="Keyset Cursor", linewidth=2)

plt.title("Cumulative Retrieval Time Comparison", fontsize=16)
plt.xlabel("Window Index (each = 500 rows)", fontsize=14)
plt.ylabel("Cumulative Time (ms)", fontsize=14)
plt.grid(True, linestyle="--", alpha=0.5)
plt.legend()

plt.tight_layout()
plt.savefig("plot_cumulative_time.png", dpi=300)
plt.show()

# ------------------------------
# Print summary statistics
# ------------------------------

print("\n=== SUMMARY STATISTICS (Total Time) ===")
print(f"Offset Pagination total time: {page_df['total_time_ms'].sum():,.0f} ms")
print(f"Slice Pagination total time : {slice_df['total_time_ms'].sum():,.0f} ms")
print(f"Keyset Cursor total time    : {keyset_df['total_time_ms'].sum():,.0f} ms")
