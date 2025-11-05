import { NitroModules } from 'react-native-nitro-modules';
import type { TtlockNitro } from './TtlockNitro.nitro';

const TtlockNitroHybridObject =
  NitroModules.createHybridObject<TtlockNitro>('TtlockNitro');

export function multiply(a: number, b: number): number {
  return TtlockNitroHybridObject.multiply(a, b);
}
