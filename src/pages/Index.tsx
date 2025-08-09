import { Button } from "@/components/ui/button";

const Index = () => {
  return (
    <main className="min-h-screen bg-background">
      <header className="container mx-auto py-16 text-center">
        <h1 className="text-4xl md:text-5xl font-bold tracking-tight mb-4">
          Minecraft Spiral Blocks Plugin — Paper 1.21.8
        </h1>
        <p className="text-lg md:text-xl text-muted-foreground max-w-2xl mx-auto">
          Create mesmerizing floating block spirals with smooth interpolation. Fully configurable radius, height, speed, block type, and particle density.
        </p>
        <div className="mt-8 flex items-center justify-center gap-4">
          <a href="#build"><Button>Build & Install</Button></a>
          <a href="/PLAN.md" target="_blank" rel="noreferrer"><Button variant="outline">View Plan</Button></a>
        </div>
      </header>

      <section className="container mx-auto px-4 pb-24 grid gap-8 md:grid-cols-3">
        <article className="rounded-lg border p-6">
          <h2 className="text-xl font-semibold mb-2">Dynamic Helix</h2>
          <p className="text-muted-foreground">Smoothly animated spiral of floating BlockDisplay entities with interpolation.</p>
        </article>
        <article className="rounded-lg border p-6">
          <h2 className="text-xl font-semibold mb-2">Full Control</h2>
          <p className="text-muted-foreground">Adjust radius, height, speed, block type, and particle density at runtime.</p>
        </article>
        <article className="rounded-lg border p-6">
          <h2 className="text-xl font-semibold mb-2">Simple Commands</h2>
          <p className="text-muted-foreground">/spiral start [radius] [height] [speed] [block] [particles], /spiral stop</p>
        </article>
      </section>

      <section id="build" className="container mx-auto px-4 pb-24">
        <h2 className="text-2xl font-semibold mb-4">Build & Install</h2>
        <ol className="list-decimal list-inside space-y-2 text-muted-foreground">
          <li>Open the <code>minecraft-spiral-plugin</code> folder in your IDE.</li>
          <li>Run <code>mvn -q -DskipTests package</code>.</li>
          <li>Copy the JAR from <code>target/</code> to your Paper server <code>plugins/</code> folder.</li>
          <li>Start the server and use <code>/spiral start</code>.</li>
        </ol>
      </section>
    </main>
  );
};

export default Index;
