// color.service.ts
import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class ColorService {
    // Tableau de couleurs prédéfinies
    private readonly predefinedColors = [
        '#4dc9f6', '#f67019', '#f53794', '#537bc4', '#acc236',
        '#166a8f', '#00a950', '#58595b', '#8549ba', '#e6194b'
    ];

    /**
     * Retourne une couleur du tableau de couleurs prédéfinies
     */
    getColorForIndex(index: number): string {
        return this.predefinedColors[index % this.predefinedColors.length];
    }

    /**
     * Ajuste la luminosité d'une couleur pour créer des variantes
     */
    adjustColor(baseColor: string, totalVariants: number, variantIndex: number): string {
        const hsl = this.hexToHSL(baseColor);
        const lightnessDelta = 15; // en pourcentage
        const adjustment = lightnessDelta * (variantIndex / totalVariants);
        hsl.l = Math.min(90, hsl.l + adjustment);
        return this.hslToHex(hsl.h, hsl.s, hsl.l);
    }

    /**
     * Assombrit une couleur
     */
    darkenColor(color: string): string {
        const hsl = this.hexToHSL(color);
        hsl.l = Math.max(0, hsl.l - 10);
        return this.hslToHex(hsl.h, hsl.s, hsl.l);
    }

    /**
     * Convertit une couleur hexadécimale en HSL
     */
    hexToHSL(hex: string): { h: number, s: number, l: number } {
        let r = 0, g = 0, b = 0;
        hex = hex.replace('#', '');

        if (hex.length === 3) {
            r = parseInt(hex.substring(0, 1).repeat(2), 16) / 255;
            g = parseInt(hex.substring(1, 2).repeat(2), 16) / 255;
            b = parseInt(hex.substring(2, 3).repeat(2), 16) / 255;
        } else if (hex.length === 6) {
            r = parseInt(hex.substring(0, 2), 16) / 255;
            g = parseInt(hex.substring(2, 4), 16) / 255;
            b = parseInt(hex.substring(4, 6), 16) / 255;
        }

        const max = Math.max(r, g, b), min = Math.min(r, g, b);
        let h = 0, s = 0, l = (max + min) / 2;

        if (max !== min) {
            const d = max - min;
            s = l > 0.5 ? d / (2 - max - min) : d / (max + min);

            switch (max) {
                case r: h = (g - b) / d + (g < b ? 6 : 0); break;
                case g: h = (b - r) / d + 2; break;
                case b: h = (r - g) / d + 4; break;
            }

            h /= 6;
        }

        return {
            h: h * 360,
            s: s * 100,
            l: l * 100
        };
    }

    /**
     * Convertit HSL en hexadécimal
     */
    hslToHex(h: number, s: number, l: number): string {
        h /= 360;
        s /= 100;
        l /= 100;

        let r, g, b;

        if (s === 0) {
            r = g = b = l;
        } else {
            const hue2rgb = (p: number, q: number, t: number) => {
                if (t < 0) t += 1;
                if (t > 1) t -= 1;
                if (t < 1 / 6) return p + (q - p) * 6 * t;
                if (t < 1 / 2) return q;
                if (t < 2 / 3) return p + (q - p) * (2 / 3 - t) * 6;
                return p;
            };

            const q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            const p = 2 * l - q;

            r = hue2rgb(p, q, h + 1 / 3);
            g = hue2rgb(p, q, h);
            b = hue2rgb(p, q, h - 1 / 3);
        }

        const toHex = (x: number) => {
            const hex = Math.round(x * 255).toString(16);
            return hex.length === 1 ? '0' + hex : hex;
        };

        return `#${toHex(r)}${toHex(g)}${toHex(b)}`;
    }

    /**
     * Convertit une couleur hexadécimale en RGBA
     */
    hexToRgba(hex: string, alpha: number): string {
        let r = 0, g = 0, b = 0;
        hex = hex.replace('#', '');

        if (hex.length === 3) {
            r = parseInt(hex.substring(0, 1).repeat(2), 16);
            g = parseInt(hex.substring(1, 2).repeat(2), 16);
            b = parseInt(hex.substring(2, 3).repeat(2), 16);
        } else if (hex.length === 6) {
            r = parseInt(hex.substring(0, 2), 16);
            g = parseInt(hex.substring(2, 4), 16);
            b = parseInt(hex.substring(4, 6), 16);
        }

        return `rgba(${r}, ${g}, ${b}, ${alpha})`;
    }


}
