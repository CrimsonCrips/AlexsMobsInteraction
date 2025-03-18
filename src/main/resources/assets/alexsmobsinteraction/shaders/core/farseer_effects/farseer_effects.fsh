#version 150

#define PI 3.1415926538
#define INV_PI 1/3.1415926538

uniform vec4 ColorModulator;
uniform float GameTime;
uniform sampler2D Sampler0;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec2 offset = vec2(0.5, 0.5) - texCoord0;
    float controlAlpha = ColorModulator.a;

    float distFromCenter = length(offset) * 2.0 + mix(-0.75, 0.0, controlAlpha);
    float coneGradient = mix(-2.0, 1.0, distFromCenter);
    float alpha = clamp(coneGradient, 0.0, 1.0);

    float angleAroundCenter = atan(offset.y, offset.x) - PI;
    float squiggles = angleAroundCenter * 64.0 + sin(GameTime * 2000.0 + distFromCenter * 25.0) * 2.0;
    float rayFromAngle = sin(squiggles - GameTime * 20.0);

    float radius = distFromCenter * 10.0 + GameTime * 100.0 + angleAroundCenter * INV_PI * 2.0;
    float degrees = squiggles * 0.0625 + GameTime * 150.0;
    vec2 polarCoords = vec2(radius, degrees * INV_PI);

    vec4 colorFromTexture = texture(Sampler0, polarCoords * 0.25 + vec2(0.0, GameTime * 50.0));

    vec4 color = (rayFromAngle * 0.5 + 0.5) * sin(colorFromTexture * PI * 4.0 + GameTime * 2000.0);
    fragColor = vec4(color.rgb * ColorModulator.rgb, alpha);
}
