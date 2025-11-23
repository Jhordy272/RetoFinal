import re
import matplotlib.pyplot as plt

def parse_results(filename):
    """Parse the results.txt file and extract latency statistics."""
    data = {
        'users': [],
        'min': [],
        'average': [],
        'median': [],
        'p90': [],
        'p95': [],
        'max': []
    }

    with open(filename, 'r') as f:
        content = f.read()

    # Split by user count blocks
    blocks = re.split(r'(\d+) ->', content)

    for i in range(1, len(blocks), 2):
        users = int(blocks[i])
        stats_block = blocks[i + 1]

        # Extract statistics
        min_val = float(re.search(r'Min:\s+([\d.]+)', stats_block).group(1))
        avg_val = float(re.search(r'Average:\s+([\d.]+)', stats_block).group(1))
        median_val = float(re.search(r'Median:\s+([\d.]+)', stats_block).group(1))
        p90_val = float(re.search(r'P90:\s+([\d.]+)', stats_block).group(1))
        p95_val = float(re.search(r'P95:\s+([\d.]+)', stats_block).group(1))
        max_val = float(re.search(r'Max:\s+([\d.]+)', stats_block).group(1))

        data['users'].append(users)
        data['min'].append(min_val)
        data['average'].append(avg_val)
        data['median'].append(median_val)
        data['p90'].append(p90_val)
        data['p95'].append(p95_val)
        data['max'].append(max_val)

    return data

def plot_results(data):
    """Create visualizations of the load test results."""
    fig, axes = plt.subplots(2, 2, figsize=(15, 10))
    fig.suptitle('Load Test Results', fontsize=16, fontweight='bold')

    # Plot 1: Average Latency
    axes[0, 0].plot(data['users'], data['average'], marker='o', linewidth=2, color='#2E86AB')
    axes[0, 0].set_xlabel('Number of Users')
    axes[0, 0].set_ylabel('Latency (ms)')
    axes[0, 0].set_title('Average Latency')
    axes[0, 0].grid(True, alpha=0.3)

    # Plot 2: Min, Median, Max
    axes[0, 1].plot(data['users'], data['min'], marker='o', label='Minimum', linewidth=2)
    axes[0, 1].plot(data['users'], data['median'], marker='s', label='Median', linewidth=2)
    axes[0, 1].plot(data['users'], data['max'], marker='^', label='Maximum', linewidth=2)
    axes[0, 1].set_xlabel('Number of Users')
    axes[0, 1].set_ylabel('Latency (ms)')
    axes[0, 1].set_title('Latency: Min, Median, Max')
    axes[0, 1].legend()
    axes[0, 1].grid(True, alpha=0.3)

    # Plot 3: Percentiles (P90, P95)
    axes[1, 0].plot(data['users'], data['p90'], marker='o', label='P90', linewidth=2, color='#A23B72')
    axes[1, 0].plot(data['users'], data['p95'], marker='s', label='P95', linewidth=2, color='#F18F01')
    axes[1, 0].set_xlabel('Number of Users')
    axes[1, 0].set_ylabel('Latency (ms)')
    axes[1, 0].set_title('Latency Percentiles')
    axes[1, 0].legend()
    axes[1, 0].grid(True, alpha=0.3)

    # Plot 4: All metrics
    axes[1, 1].plot(data['users'], data['min'], marker='o', label='Minimum', linewidth=1.5, alpha=0.7)
    axes[1, 1].plot(data['users'], data['average'], marker='s', label='Average', linewidth=2)
    axes[1, 1].plot(data['users'], data['median'], marker='^', label='Median', linewidth=1.5, alpha=0.7)
    axes[1, 1].plot(data['users'], data['p90'], marker='d', label='P90', linewidth=1.5, alpha=0.7)
    axes[1, 1].plot(data['users'], data['p95'], marker='*', label='P95', linewidth=1.5, alpha=0.7)
    axes[1, 1].plot(data['users'], data['max'], marker='x', label='Maximum', linewidth=1.5, alpha=0.7)
    axes[1, 1].set_xlabel('Number of Users')
    axes[1, 1].set_ylabel('Latency (ms)')
    axes[1, 1].set_title('All Metrics')
    axes[1, 1].legend(loc='upper left')
    axes[1, 1].grid(True, alpha=0.3)

    plt.tight_layout(h_pad=3.0)
    plt.savefig('load_test_results.png', dpi=300, bbox_inches='tight')
    print("Graph saved as 'load_test_results.png'")
    plt.show()

def print_summary(data):
    """Print a summary of the results."""
    print("\n" + "="*60)
    print("LOAD TEST RESULTS SUMMARY")
    print("="*60)
    print(f"User range: {data['users'][0]} - {data['users'][-1]}")
    print(f"Minimum average latency: {min(data['average']):.2f} ms (at {data['users'][data['average'].index(min(data['average']))]} users)")
    print(f"Maximum average latency: {max(data['average']):.2f} ms (at {data['users'][data['average'].index(max(data['average']))]} users)")
    print(f"Minimum P95: {min(data['p95']):.2f} ms")
    print(f"Maximum P95: {max(data['p95']):.2f} ms")
    print("="*60 + "\n")

if __name__ == "__main__":
    # Parse results
    print("Reading load test results...")
    data = parse_results('results.txt')

    # Print summary
    print_summary(data)

    # Create plots
    print("Generating graphs...")
    plot_results(data)
